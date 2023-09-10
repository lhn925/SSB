package sky.board.domain.user.service.help;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.board.domain.user.dto.UserInfoDto;
import sky.board.domain.user.dto.help.UserPwResetFormDto;
import sky.board.domain.user.dto.login.CustomUserDetails;
import sky.board.domain.user.dto.myInfo.UserPwUpdateFormDto;
import sky.board.domain.user.entity.User;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.repository.UserQueryRepository;
import sky.board.global.redis.dto.RedisKeyDto;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserHelpService {

    private final UserQueryRepository userQueryRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 비밀번호 업데이트
     *
     * @param userPwResetFormDto
     * @return
     */
    @Transactional
    public UserDetails passwordUpdate(UserPwResetFormDto userPwResetFormDto) throws IllegalArgumentException {

        User findByUser = User.getOptionalUser(
            userQueryRepository.findByUserId(userPwResetFormDto.getUserId()));
        //현재 비밀번호 와 대조
        isPasswordSameAsNew(userPwResetFormDto, findByUser);
        User.updatePw(findByUser, userPwResetFormDto.getNewPw(), userPwResetFormDto.getPwSecLevel(), passwordEncoder);
        return getCustomUserDetails(findByUser);
    }

    /**
     * passwordUpdate
     *
     * @param userPwUpdateFormDto
     * @return
     */
    @Transactional
    public CustomUserDetails passwordUpdate(UserPwUpdateFormDto userPwUpdateFormDto, UserInfoDto userInfoDto) {

        User findByUser = User.getOptionalUser(
            userQueryRepository.findByUserId(userInfoDto.getUserId()));

        isPasswordSameAsNew(userPwUpdateFormDto, findByUser);
        User.updatePw(findByUser, userPwUpdateFormDto.getNewPw(), userPwUpdateFormDto.getPwSecLevel(),
            passwordEncoder);
        return getCustomUserDetails(findByUser);
    }


    private static CustomUserDetails getCustomUserDetails(User findByUser) {
        return CustomUserDetails.builder()
            .userId(findByUser.getUserId())
            .email(findByUser.getEmail())
            .uId(findByUser.getId())
            .username(findByUser.getUserId()).build();
    }


    private void isPasswordSameAsNew(UserPwResetFormDto userPwResetFormDto, User findByUser)
        throws IllegalArgumentException {
        //현재 비밀번호 와 대조
        boolean matches = passwordEncoder.matches(userPwResetFormDto.getNewPw(), findByUser.getPassword());

        // 현재 비밀번호와 바꾸려는 비밀번호가 같으면 error
        if (matches) {
            throw new IllegalArgumentException("pw.isPasswordSameAsNew");
        }
    }

    private void isPasswordSameAsNew(UserPwUpdateFormDto userPwUpdateFormDto, User findByUser)
        throws IllegalArgumentException {
        //현재 비밀번호 와 대조

        // 입력한 비밀번호가 지금 비밀번호랑 맞아야하고
        boolean authMatches = passwordEncoder.matches(userPwUpdateFormDto.getPassword(), findByUser.getPassword());
        // 현재비밀번호랑 같으면 안되고
        boolean matches = passwordEncoder.matches(userPwUpdateFormDto.getNewPw(), findByUser.getPassword());

        log.info("authMatches = {}", authMatches);
        log.info("matches = {}", matches);
        String code = null;

        if (!authMatches) {
            code = "pw.authMatches.mismatch";
        } else if (matches) {
            code = "pw.isPasswordSameAsNew";
        }
        if (code != null) {
            throw new IllegalArgumentException(code);
        }
    }

}

package sky.Sss.domain.user.service.help;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.user.dto.UserInfoDto;
import sky.Sss.domain.user.dto.help.UserPwResetFormDto;
import sky.Sss.domain.user.dto.login.CustomUserDetails;
import sky.Sss.domain.user.dto.myInfo.UserPwUpdateFormDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.repository.UserQueryRepository;
import sky.Sss.domain.user.service.UserQueryService;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserHelpService {

    private final PasswordEncoder passwordEncoder;
    private final UserQueryService userQueryService;

    /**
     * 비밀번호 업데이트
     *
     * @param userPwResetFormDto
     * @return
     */
    @Transactional
    public UserDetails passwordUpdate(UserPwResetFormDto userPwResetFormDto) throws IllegalArgumentException {

        User findUser = userQueryService.findOne(userPwResetFormDto.getUserId());
        //현재 비밀번호 와 대조
        isPasswordSameAsNew(userPwResetFormDto, findUser);
        User.updatePw(findUser, userPwResetFormDto.getNewPw(), userPwResetFormDto.getPwSecLevel(), passwordEncoder);
        return getCustomUserDetails(findUser);
    }

    /**
     * passwordUpdate
     *
     * @param userPwUpdateFormDto
     * @return
     */
    @Transactional
    public CustomUserDetails myPagePwUpdate(UserPwUpdateFormDto userPwUpdateFormDto) {
        User findUser = userQueryService.findOne();

        isPasswordSameAsNew(userPwUpdateFormDto, findUser);
        User.updatePw(findUser, userPwUpdateFormDto.getNewPw(), userPwUpdateFormDto.getPwSecLevel(),
            passwordEncoder);
        return getCustomUserDetails(findUser);
    }


    private static CustomUserDetails getCustomUserDetails(User findByUser) {
        return (CustomUserDetails) User.UserBuilder(findByUser);
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

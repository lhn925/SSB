package sky.Sss.domain.user.service.join;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.user.dto.join.UserJoinAgreeDto;
import sky.Sss.domain.user.dto.join.UserJoinPostDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.UserJoinAgreement;
import sky.Sss.domain.user.exception.DuplicateCheckException;
import sky.Sss.domain.user.exception.UserJoinServerErrorException;
import sky.Sss.domain.user.repository.join.UserAgreeRepository;

import sky.Sss.domain.user.repository.join.UserJoinRepository;
import sky.Sss.domain.user.repository.UserQueryRepository;
import sky.Sss.domain.user.utili.PwEncryptor;


@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserJoinService {

    private final MessageSource ms;
    private final UserAgreeRepository UserAgreeRepository;
    private final UserQueryRepository userQueryRepository;
    private final UserJoinRepository userJoinRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long join(UserJoinPostDto userJoinDto, UserJoinAgreeDto userJoinAgreeDto) {

        // 유저토큰 생성 할 객체 생성
        PwEncryptor pwEncryptor = new PwEncryptor();
        String salt = pwEncryptor.getSALT();


        // 중복검사
        joinDuplicate(userJoinDto, salt);

        // db 저장
        User user = User.createJoinUser(userJoinDto, salt, passwordEncoder);
        userJoinRepository.save(user);
        if (user.getId() == null) {
            throw new UserJoinServerErrorException(ms.getMessage("join.error", null, null));
        }
        // 이용약관 저장
        UserJoinAgreement userJoinAgreement = UserJoinAgreement.createUserJoinAgreement(user, userJoinAgreeDto);

        UserAgreeRepository.save(userJoinAgreement);

        return user.getId();
    }

    private void joinDuplicate(UserJoinPostDto userJoinDto, String salt) {
        checkId(userJoinDto.getUserId());
        checkUserName(userJoinDto.getUserName());
        checkEmail(userJoinDto.getEmail());
        checkSalt(salt);
    }

    public void checkSalt(String salt) {
        if (userQueryRepository.existsBySalt(salt)) {
            throw new UserJoinServerErrorException(ms.getMessage("join.error", null, null));
        }
    }

    public void checkId(String userId) {
        if (userQueryRepository.existsByUserId(userId)) {
            throw new DuplicateCheckException("아이디", "userId", userId);
        }
    }

    public void checkUserName(String userName) {
        if (userQueryRepository.existsByUserName(userName)) {
            throw new DuplicateCheckException("닉네임", "userName", userName);
        }

    }

    public void checkEmail(String email) {
        if (userQueryRepository.existsByEmail(email)) {
            throw new DuplicateCheckException("email", "email", email);
        }
    }

}
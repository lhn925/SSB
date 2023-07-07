package sky.board.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import sky.board.domain.user.dto.UserJoinAgreeDto;
import sky.board.domain.user.dto.UserJoinPostDto;
import sky.board.domain.user.entity.User;
import sky.board.domain.user.entity.UserJoinAgreement;
import sky.board.domain.user.ex.DuplicateCheckException;
import sky.board.domain.user.ex.UserJoinServerErrorException;
import sky.board.domain.user.repository.UserAgreeRepository;
import sky.board.domain.user.repository.UserJoinRepository;

import sky.board.domain.user.repository.UserQueryRepository;
import sky.board.domain.user.utill.PwEncryptor;


@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserJoinService {

    private final MessageSource ms;
    private final UserAgreeRepository UserAgreeRepository;
    private final UserQueryRepository userQueryRepository;

    @Transactional
    public Long join(UserJoinPostDto userJoinDto, UserJoinAgreeDto userJoinAgreeDto) {

        // 암호화 객체 생성
        PwEncryptor pwEncryptor = new PwEncryptor();

        //암호화에 사용될 salt 생성
        // hashingPw = salt + 사용자가 입력한 pw 암호화
        String salt = pwEncryptor.getSALT();

        userJoinDto.changePassword(pwEncryptor.hashing(userJoinDto.getPassword().getBytes(), salt));

        // 중복검사
        joinDuplicate(userJoinDto, salt);

        // db 저장
        User user = User.createJoinUser(userJoinDto, salt);

        // 이용약관 저장
        UserJoinAgreement userJoinAgreement = UserJoinAgreement.createUserJoinAgreement(user, userJoinAgreeDto);

        UserAgreeRepository.save(userJoinAgreement);

        if (user.getId() == null) {
            throw new UserJoinServerErrorException(ms.getMessage("join.error", null, null));
        }
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
            throw new DuplicateCheckException(
                ms.getMessage("join.duplication", new Object[]{"salt"}, null));
        }
    }

    public void checkId(String userId) throws DuplicateCheckException {
        if (userQueryRepository.existsByUserId(userId)) {
            throw new DuplicateCheckException("아이디");
        }
    }

    public void checkUserName(String userName) {
        if (userQueryRepository.existsByUserName(userName)) {
            throw new DuplicateCheckException("닉네임");
        }

    }

    public void checkEmail(String email) {
        Boolean aBoolean = userQueryRepository.existsByEmail(email);

        System.out.println("aBoolean = " + aBoolean);
        if (userQueryRepository.existsByEmail(email)) {
            throw new DuplicateCheckException("email");
        }
    }

}

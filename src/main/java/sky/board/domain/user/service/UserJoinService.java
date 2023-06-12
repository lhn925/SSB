package sky.board.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.board.domain.user.dto.UserJoinDto;
import sky.board.domain.user.entity.User;
import sky.board.domain.user.ex.DuplicateCheckException;
import sky.board.domain.user.ex.UserJoinServerErrorException;
import sky.board.domain.user.repository.UserJoinRepository;

import sky.board.domain.user.repository.UserQueryRepository;
import sky.board.domain.user.utill.PwEncryptor;


@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserJoinService {

    private final MessageSource ms;
    private final UserJoinRepository userJoinRepository;
    private final UserQueryRepository userQueryRepository;

    @Transactional
    public Long join(UserJoinDto userJoinDto) {

        // 암호화 객체 생성
        PwEncryptor pwEncryptor = new PwEncryptor();

        //암호화에 사용될 salt 생성
        // hashingPw = salt + 사용자가 입력한 pw 암호화
        String salt = pwEncryptor.getSALT();
        userJoinDto.setPassword(pwEncryptor.Hashing(userJoinDto.getPassword().getBytes(), salt));

        // 중복검사
        joinDuplicate(userJoinDto, salt);

        // db 저장
        User user = User.createUser(userJoinDto, salt);
        userJoinRepository.save(user);

        if (user.getId() == null) {
            throw new UserJoinServerErrorException(ms.getMessage("join.error", null, null));
        }

        return user.getId();
    }

    private void joinDuplicate(UserJoinDto userJoinDto, String salt) {
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

    public void checkId(String userId) {
        if (userQueryRepository.existsByUserId(userId)) {
            throw new DuplicateCheckException(
                ms.getMessage("join.duplication", new Object[]{"아이디"}, null));
        }
    }

    public void checkUserName(String userName) {
        if (userQueryRepository.existsByUserName(userName)) {
            throw new DuplicateCheckException(
                ms.getMessage("join.duplication", new Object[]{"닉네임"}, null));
        }
    }

    public void checkEmail(String email) {
        Boolean aBoolean = userQueryRepository.existsByEmail(email);

        System.out.println("aBoolean = " + aBoolean);
        if (userQueryRepository.existsByEmail(email)) {
            throw new DuplicateCheckException(
                ms.getMessage("join.duplication", new Object[]{"email"}, null));
        }
    }

}

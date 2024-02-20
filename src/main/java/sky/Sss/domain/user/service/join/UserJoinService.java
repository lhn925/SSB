package sky.Sss.domain.user.service.join;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
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
    private final UserAgreeRepository userAgreeRepository;
    private final UserQueryRepository userQueryRepository;
    private final UserJoinRepository userJoinRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional

    // CacheEvict: value 에 해당되는 캐시값 삭제
    @Caching(evict = {
        @CacheEvict(value = {"checkId"},key = "#userJoinDto.userId"),
        @CacheEvict(value = {"checkUserName"},key = "#userJoinDto.userName"),
        @CacheEvict(value = {"checkEmail"},key = "#userJoinDto.email"),
    })
    public Long join(UserJoinPostDto userJoinDto) {

        // 유저토큰 생성 할 객체 생성
        PwEncryptor pwEncryptor = new PwEncryptor();
        String salt = pwEncryptor.getSALT();

        // 중복검사
        checkSalt(salt);

        // db 저장
        User user = User.createJoinUser(userJoinDto, salt, passwordEncoder);
        userJoinRepository.save(user);
        if (user.getId() == null) {
            throw new UserJoinServerErrorException(ms.getMessage("join.error", null, null));
        }
        // 이용약관 저장
        UserJoinAgreement userJoinAgreement = UserJoinAgreement.createUserJoinAgreement(user, userJoinDto);

        userAgreeRepository.save(userJoinAgreement);

        return user.getId();
    }

    public void checkSalt(String salt) {
        if (userQueryRepository.existsBySalt(salt)) {
            throw new UserJoinServerErrorException(ms.getMessage("join.error", null, null));
        }
    }

    public void duplicateCheckJoin(UserJoinPostDto userJoinPostDto, BindingResult bindingResult)
        throws DuplicateCheckException {
        checkId(userJoinPostDto.getUserId(), bindingResult);
        checkEmail(userJoinPostDto.getEmail(), bindingResult);
        checkUserName(userJoinPostDto.getUserName(), bindingResult);
    }

    @Cacheable(value = "checkId", key = "#userId", cacheManager = "contentCacheManager")
    public void checkId(String userId, BindingResult bindingResult) throws DuplicateCheckException {
        if (userQueryRepository.existsByUserId(userId)) {
            throw new DuplicateCheckException(bindingResult, "아이디", "userId", userId);
        }
    }

    @Cacheable(value = "checkUserName", key = "#userName", cacheManager = "contentCacheManager")
    public void checkUserName(String userName, BindingResult bindingResult) throws DuplicateCheckException {
        if (userQueryRepository.existsByUserName(userName)) {
            throw new DuplicateCheckException(bindingResult, "닉네임", "userName", userName);
        }
    }

    @Cacheable(value = "checkEmail", key = "#email", cacheManager = "contentCacheManager")
    public void checkEmail(String email, BindingResult bindingResult) throws DuplicateCheckException {
        if (userQueryRepository.existsByEmail(email)) {
            throw new DuplicateCheckException(bindingResult, "email", "email", email);
        }
    }

}

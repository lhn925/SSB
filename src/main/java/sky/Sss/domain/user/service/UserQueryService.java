package sky.Sss.domain.user.service;


import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.user.dto.UserInfoDto;
import sky.Sss.domain.user.dto.login.CustomUserDetails;
import sky.Sss.domain.user.dto.myInfo.UserMyInfoDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.exception.UserInfoNotFoundException;
import sky.Sss.domain.user.exception.UserNotFoundException;
import sky.Sss.domain.user.model.Enabled;
import sky.Sss.domain.user.model.UserGrade;
import sky.Sss.domain.user.repository.UserQueryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserQueryRepository userQueryRepository;


    public CustomUserDetails findByEmailOne(String email, Enabled enabled)
        throws UsernameNotFoundException {
        Optional<User> findOne = userQueryRepository.findByEmailAndIsEnabled(email, enabled.getValue());
        User user = findOne.orElseThrow(() -> new UsernameNotFoundException("email.notfound"));

        return (CustomUserDetails) User.UserBuilder(user);
    }

    public CustomUserDetails findStatusUserId(String userId, Enabled enabled)
        throws UsernameNotFoundException {
        Optional<User> findOne = userQueryRepository.findByUserIdAndIsEnabled(userId, enabled.getValue());
        User user = findOne.orElseThrow(() -> new UsernameNotFoundException("userId.notfound"));

        return (CustomUserDetails) User.UserBuilder(user);
    }


    //    @Cacheable(value = RedisKeyDto.REDIS_USER_CACHE_TOKEN_KEY,key = "#userId",cacheManager = "contentCacheManager")
    public String findTokenByUserId(String userId, Enabled enabled)
        throws UsernameNotFoundException {
        Optional<User> findOne = userQueryRepository.findByUserIdAndIsEnabled(userId, enabled.getValue());
        User user = findOne.orElseThrow(() -> new UsernameNotFoundException("userId.notfound"));
        return user.getToken();
    }

    public UserInfoDto findByUser(UserInfoDto userInfoDto)
        throws UsernameNotFoundException {
        User user = User.getOptionalUser(
            userQueryRepository.findByUserId(userInfoDto.getUserId()));

        UserDetails userDetails = User.UserBuilder(user);

        return UserInfoDto.createUserInfo(userDetails);
    }

    public Set<User> findUsersByUserNames(Set<String> userNames,Enabled isEnabled)
        throws UsernameNotFoundException {
        return userQueryRepository.findAllByUserNames(userNames,isEnabled.getValue());
    }


    public User findOne() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority
        ).findFirst().orElse(null);
        if (authorities == null || authorities.equals(UserGrade.ANONYMOUS.getRole())) {
            throw new UserInfoNotFoundException("sky.userId.notFind");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // getUsername 은 여기선 UserId를 뜻함
        // userName
        // userId
        // userToken
        Optional<User> optionalUser = userQueryRepository.findByUserId(userDetails.getUsername());
        return User.getOptionalUser(optionalUser);
    }

    public UserMyInfoDto getUserMyInfoDto() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userQueryRepository.getUserMyInfoDto(userDetails.getUsername())
            .orElseThrow(UserInfoNotFoundException::new);
    }

    public User findOne(String userId) {
        Optional<User> optionalUser = userQueryRepository.findByUserId(userId);
        return User.getOptionalUser(optionalUser);
    }

    public User findOne(String userId, String token) {
        Optional<User> optionalUser = userQueryRepository.findOne(userId, token);
        return User.getOptionalUser(optionalUser);
    }

    public User findOne(Long uid, Enabled enabled) {
        Optional<User> optionalUser = userQueryRepository.findByIdAndIsEnabled(uid, enabled.getValue());
        return User.getOptionalUser(optionalUser);
    }


    public Optional<User> findOptionalUser(String userId) {
        return userQueryRepository.findByUserId(userId);
    }


}

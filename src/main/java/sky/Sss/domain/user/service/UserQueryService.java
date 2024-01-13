package sky.Sss.domain.user.service;


import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import javax.naming.AuthenticationNotSupportedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import sky.Sss.domain.user.dto.UserInfoDto;
import sky.Sss.domain.user.dto.login.CustomUserDetails;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.exception.UserInfoNotFoundException;
import sky.Sss.domain.user.model.Enabled;
import sky.Sss.domain.user.model.UserGrade;
import sky.Sss.domain.user.repository.UserQueryRepository;
import sky.Sss.global.redis.dto.RedisKeyDto;

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

    public UserInfoDto findByUser(UserInfoDto userInfoDto)
        throws UsernameNotFoundException {
        User user = User.getOptionalUser(
            userQueryRepository.findByUserId(userInfoDto.getUserId()));

        UserDetails userDetails = User.UserBuilder(user);

        return UserInfoDto.createUserInfo(userDetails);
    }

    public User findOne() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authorities = authentication.getAuthorities().stream().map(grantedAuthority -> grantedAuthority.getAuthority()
        ).findFirst().orElse(null);
        if (authorities.equals(UserGrade.ANONYMOUS.getRole())) {
            throw new UserInfoNotFoundException("sky.userId.notFind");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> optionalUser = userQueryRepository.findByUserId(userDetails.getUsername());
        User user = User.getOptionalUser(optionalUser);
        return user;
    }

    public UserInfoDto getUserInfoDto() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = findOne(userDetails.getUsername());
        UserInfoDto userInfoDto = UserInfoDto.createUserInfo(user);
        return userInfoDto;
    }

    public User findOne(String userId) {
        Optional<User> optionalUser = userQueryRepository.findByUserId(userId);
        User user = User.getOptionalUser(optionalUser);
        return user;
    }

    public User findOne(String userId, String token) {
        Optional<User> optionalUser = userQueryRepository.findOne(userId, token);
        User user = User.getOptionalUser(optionalUser);
        return user;
    }

    public Optional<User> findOptionalUser(String userId) {
        Optional<User> optionalUser = userQueryRepository.findByUserId(userId);
        return optionalUser;
    }


}

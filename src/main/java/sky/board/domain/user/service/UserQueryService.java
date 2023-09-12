package sky.board.domain.user.service;


import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.board.domain.user.dto.UserInfoDto;
import sky.board.domain.user.dto.login.CustomUserDetails;
import sky.board.domain.user.entity.User;
import sky.board.domain.user.model.Enabled;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.repository.UserQueryRepository;
import sky.board.global.redis.dto.RedisKeyDto;

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

    public User findOne(HttpSession session) {
        UserInfoDto userInfoDto = (UserInfoDto) session.getAttribute(RedisKeyDto.USER_KEY);

        Optional<User> optionalUser = userQueryRepository.findOne(userInfoDto.getUserId(), userInfoDto.getToken());

        User user = User.getOptionalUser(optionalUser);
        return user;
    }

    public User findOne(String userId) {
        Optional<User> optionalUser = userQueryRepository.findByUserId(userId);
        User user = User.getOptionalUser(optionalUser);
        return user;
    }

    public User findOne(String userId,String token) {
        Optional<User> optionalUser = userQueryRepository.findOne(userId,token);
        User user = User.getOptionalUser(optionalUser);
        return user;
    }


}

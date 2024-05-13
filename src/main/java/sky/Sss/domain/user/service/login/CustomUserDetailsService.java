package sky.Sss.domain.user.service.login;


import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.user.dto.redis.RedisUserDTO;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.exception.LoginBlockException;
import sky.Sss.domain.user.repository.UserQueryRepository;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.global.locationfinder.dto.UserLocationDto;
import sky.Sss.global.locationfinder.service.LocationFinderService;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserQueryService userQueryService;
    private final LocationFinderService locationFinderService;
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User findUser = userQueryService.getOptUserEntity(userId)
            .orElseThrow(() -> new UsernameNotFoundException("login.NotFound"));
        // cache 저장
        userQueryService.setUserInfoDtoRedis(RedisUserDTO.create(findUser));
        return User.UserBuilder(findUser);
    }



}

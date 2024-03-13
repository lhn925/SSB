package sky.Sss.domain.user.service.login;


import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.exception.LoginBlockException;
import sky.Sss.domain.user.repository.UserQueryRepository;
import sky.Sss.global.locationfinder.dto.UserLocationDto;
import sky.Sss.global.locationfinder.service.LocationFinderService;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserQueryRepository userQueryRepository;
    private final LocationFinderService locationFinderService;
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        log.info("loadUserByUsername userId = {}", userId);
        User findUser = userQueryRepository.findByUserId(userId)
            .orElseThrow(() -> new UsernameNotFoundException("login.NotFound"));
        return User.UserBuilder(findUser);
    }



}

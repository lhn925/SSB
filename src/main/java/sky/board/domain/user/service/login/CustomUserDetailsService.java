package sky.board.domain.user.service.login;


import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.board.domain.user.dto.help.UserPwResetFormDto;
import sky.board.domain.user.dto.login.CustomUserDetails;
import sky.board.domain.user.entity.User;
import sky.board.domain.user.repository.UserQueryRepository;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserQueryRepository userQueryRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        log.info("loadUserByUsername userId = {}", userId);
        User findUser = userQueryRepository.findByUserId(userId)
            .orElseThrow(() -> new UsernameNotFoundException("login.NotFound"));
        return User.UserBuilder(findUser);
    }

}

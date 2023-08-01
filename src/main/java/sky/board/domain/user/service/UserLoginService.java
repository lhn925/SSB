package sky.board.domain.user.service;


import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.board.domain.user.entity.User;
import sky.board.domain.user.repository.UserQueryRepository;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserLoginService implements UserDetailsService {

    private final UserQueryRepository userQueryRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Optional<User> findUser = userQueryRepository.findByUserId(userId);
        User user = findUser.orElseThrow(() -> new UsernameNotFoundException("login.NotFound"));
        return User.UserBuilder(user);
    }
}
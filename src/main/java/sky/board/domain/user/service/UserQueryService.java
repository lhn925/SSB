package sky.board.domain.user.service;


import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.board.domain.user.dto.help.UserHelpIdDto;
import sky.board.domain.user.dto.login.CustomUserDetails;
import sky.board.domain.user.dto.login.CustomUserDetails.CustomUserDetailsBuilder;
import sky.board.domain.user.entity.User;
import sky.board.domain.user.repository.UserQueryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserQueryRepository userQueryRepository;
    private final MessageSource ms;


    public CustomUserDetails findByEmailOne(String email)
        throws UsernameNotFoundException {
        Optional<User> findOne = userQueryRepository.findByEmail(email);
        User user = findOne.orElseThrow(() -> new UsernameNotFoundException("userName.notfound"));

        return CustomUserDetails.builder()
            .userId(user.getUserId())
            .nickname(user.getUserName())
            .createdDateTime(user.getCreatedDateTime()).build();
    }


}

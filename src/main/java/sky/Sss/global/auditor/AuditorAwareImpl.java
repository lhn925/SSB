package sky.Sss.global.auditor;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import sky.Sss.domain.user.model.UserGrade;


@Slf4j
@Getter
@Setter(value = PRIVATE)
public class AuditorAwareImpl implements AuditorAware<String> {

    private String userId;

    public AuditorAwareImpl() {
        this.userId = "";
    }

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            List<? extends GrantedAuthority> grantedAuthorities = authentication.getAuthorities().stream().toList();
            // anonymous 가 아니면 userDetails 객체 생성 후 userId

            log.info("grantedAuthorities.size() = {}", grantedAuthorities.size());
            for (GrantedAuthority auth : grantedAuthorities) {
                log.info("auth = {}", auth);

                if (!auth.getAuthority().equals(UserGrade.ANONYMOUS.getRole())) {
                    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                    AuditorAwareImpl.changeUserId(this, userDetails.getUsername());
                }
            }
        }

        log.info("this.userId = {}", this.userId);
        return Optional.of(this.userId);
    }

    public static void changeUserId(AuditorAware auditorAware, String userId) {
        AuditorAwareImpl auditorAwareImpl = (AuditorAwareImpl) auditorAware;
        auditorAwareImpl.setUserId(userId);
    }

}

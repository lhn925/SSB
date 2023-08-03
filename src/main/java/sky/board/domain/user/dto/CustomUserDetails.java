package sky.board.domain.user.dto;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;


/**
 * getAuthorities() : 계정이 가지고 있는 권한 목록 리턴
 * getPassword() : 계정의 비밀번호 리턴
 * getUsername() : 계정 이름 리턴
 * isAccountNonExpired() : 계정이 만료됐는지 리턴 -> true는 완료되지 않음 의미
 * isAccountNonLocked() : 계정이 잠겨있는지 리턴 -> true는 잠기지 않음
 * isCredentialNonExpired() : 비밀번호가 만료됐는지 리턴 -> true는 만료X 의미
 * isEnabled() : 계정이 활성화돼 있는지 리턴 -> true는 활성화 상태 의미
 */

@Slf4j
public class CustomUserDetails implements UserDetails, CredentialsContainer {

    private String url;

    private String password;
    private String username;

    private List<GrantedAuthority> authorities;

    // isAccountNonExpired() : 계정이 만료됐는지 리턴 -> true는 완료되지 않음 의미
    private boolean accountNonExpired;

    // isAccountNonLocked() : 계정이 잠겨있는지 리턴 -> true는 잠기지 않음
    private boolean accountNonLocked;


    //isCredentialNonExpired() : 비밀번호가 만료됐는지 리턴 -> true는 만료X 의미
    private boolean credentialsNonExpired;


    // isEnabled() : 계정이 활성화돼 있는지 리턴 -> true는 활성화 상태 의미
    private boolean enabled;

    private Function<String, String> passwordEncoder = (password) -> password;


    @Builder
    public CustomUserDetails(String url, String password, String username,
        boolean accountNonExpired, boolean accountNonLocked, boolean credentialsNonExpired, boolean enabled) {
        this.url = url;
        this.password = this.passwordEncoder.apply(password);
        log.info("password {}", password);
        this.username = username;
        this.accountNonExpired = !accountNonExpired;
        this.accountNonLocked = !accountNonLocked;
        this.credentialsNonExpired = !credentialsNonExpired;
        this.enabled = !enabled;

        log.info(" accountNonExpired{}", this.accountNonExpired);
        log.info(" accountNonLocked{}", this.accountNonLocked);
        log.info("credentialsNonExpired {}", this.credentialsNonExpired);
        log.info(" enabled {}", this.enabled);


    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName()).append(" [");
        sb.append("Username=").append(this.username).append(", ");
        sb.append("Password=[PROTECTED], ");
        sb.append("Enabled=").append(this.enabled).append(", ");
        sb.append("url=").append(this.url).append(", ");
        sb.append("AccountNonExpired=").append(this.accountNonExpired).append(", ");
        sb.append("credentialsNonExpired=").append(this.credentialsNonExpired).append(", ");
        sb.append("AccountNonLocked=").append(this.accountNonLocked).append(", ");
        sb.append("Granted Authorities=").append(this.authorities).append("]");
        return sb.toString();
    }


    public List<GrantedAuthority> roles(String... roles) {
        List<GrantedAuthority> authorities = new ArrayList<>(roles.length);
        for (String role : roles) {
            Assert.isTrue(!role.startsWith("ROLE_"),
                () -> role + " cannot start with ROLE_ (it is automatically added)");
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        return authorities;
    }

    @Override
    public void eraseCredentials() {

    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    public void setAuthorities(String... roles) {
        this.authorities = roles(roles);
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

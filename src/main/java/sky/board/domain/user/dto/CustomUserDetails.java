package sky.board.domain.user.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.Assert;
import sky.board.domain.user.entity.User;

/**
 * getAuthorities() : 계정이 가지고 있는 권한 목록 리턴
 * getPassword() : 계정의 비밀번호 리턴
 * getUsername() : 계정 이름 리턴
 * isAccountNonExpired() : 계정이 만료됐는지 리턴 -> true는 완료되지 않음 의미
 * isAccountNonLocked() : 계정이 잠겨있는지 리턴 -> true는 잠기지 않음
 * isCredentialNonExpired() : 비밀번호가 만료됐는지 리턴 -> true는 만료X 의미
 * isEnabled() : 계정이 활성화돼 있는지 리턴 -> true는 활성화 상태 의미
 */


public class CustomUserDetails implements Serializable, UserDetails {

    private String url;
    private String token;
    private String userId;
    private String password;
    private String username;
    private User user;


    private List<GrantedAuthority> authorities;

    // isAccountNonExpired() : 계정이 만료됐는지 리턴 -> true는 완료되지 않음 의미
    private boolean accountNonExpired;

    // isAccountNonLocked() : 계정이 잠겨있는지 리턴 -> true는 잠기지 않음
    private boolean accountNonLocked;


    //isCredentialNonExpired() : 비밀번호가 만료됐는지 리턴 -> true는 만료X 의미
    private boolean credentialsNonExpired;


    // isEnabled() : 계정이 활성화돼 있는지 리턴 -> true는 활성화 상태 의미
    private boolean enabled;

    private transient Function<String, String> passwordEncoder = (password) -> password;

    public CustomUserDetails() {
    }

    public CustomUserDetails(String url, String token, String userId, String password, String username, User user,
        List<GrantedAuthority> authorities, boolean accountNonExpired, boolean accountNonLocked,
        boolean credentialsNonExpired, boolean enabled, Function<String, String> passwordEncoder) {
        this.url = url;
        this.token = token;
        this.userId = userId;
        this.password = password;
        this.username = username;
        this.user = user;
        this.authorities = authorities;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
        this.passwordEncoder = passwordEncoder;
    }

    @Builder
    public CustomUserDetails(String url, String userId, String token, String password, String username,
        boolean accountNonExpired, boolean accountNonLocked, boolean credentialsNonExpired, boolean enabled,
        User user) {
        this.url = url;
        this.token = token;
        this.userId = userId;
        this.password = this.passwordEncoder.apply(password);
        this.username = username;
        this.accountNonExpired = !accountNonExpired;
        this.accountNonLocked = !accountNonLocked;
        this.credentialsNonExpired = !credentialsNonExpired;
        this.enabled = !enabled;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
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
    @JsonIgnore
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

    @Override
    public String toString() {
        return "{" +
            "url='" + url + '\'' +
            ", token='" + token + '\'' +
            ", userId='" + userId + '\'' +
            ", username='" + username + '\'' +
            ", user=" + user +
            ", authorities=" + Arrays.toString(new List[]{authorities}) +
            ", accountNonExpired=" + accountNonExpired +
            ", accountNonLocked=" + accountNonLocked +
            ", credentialsNonExpired=" + credentialsNonExpired +
            ", enabled=" + enabled +
            '}';
    }


}

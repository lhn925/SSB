package sky.Sss.domain.user.dto.login;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import lombok.Builder;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

/**
 * getAuthorities() : 계정이 가지고 있는 권한 목록 리턴
 * getPassword() : 계정의 비밀번호 리턴
 * getUserName() : 계정 이름 리턴
 * isAccountNonExpired() : 계정이 만료됐는지 리턴 -> true는 완료되지 않음 의미
 * isAccountNonLocked() : 계정이 잠겨있는지 리턴 -> true는 잠기지 않음
 * isCredentialNonExpired() : 비밀번호가 만료됐는지 리턴 -> true는 만료X 의미
 * isEnabled() : 계정이 활성화돼 있는지 리턴 -> true는 활성화 상태 의미
 */


public class CustomUserDetails implements Serializable, UserDetails,CredentialsContainer {


    private Long uId;
    private String url;
    private String token;
    private String userId;
    private String pictureUrl;
    private LocalDateTime userNameModifiedDate;

    // username 이랑 겹치는 문제로 바꿈 nickname으로 일시적으로 바꿈
    private String nickname;
    private String email;
    private String password;
    private String username;
    private boolean isLoginBlocked;

    //가입한 날짜
    private LocalDateTime createdDateTime;

    private List<GrantedAuthority> authorities;

    // isAccountNonExpired() : 계정이 만료됐는지 리턴 -> true는 완료되지 않음 의미
    private boolean accountNonExpired;

    // isAccountNonLocked() : 계정이 잠겨있는지 리턴 -> true는 잠기지 않음
    private boolean accountNonLocked;


    //isCredentialNonExpired() : 비밀번호가 만료됐는지 리턴 -> true는 만료X 의미
    private boolean credentialsNonExpired;

    public String getPictureUrl() {
        return pictureUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }


    // 자 트루가 활성화 지금 탈퇴 X , False가 탈퇴
    // isEnabled() : 계정이 활성화돼 있는지 리턴 -> true는 활성화 상태 의미
    private boolean enabled;

    private transient Function<String, String> passwordEncoder = (password) -> password;

    protected CustomUserDetails() {
    }

    public CustomUserDetails(Long uId,String url, String token, String userId, String password, String username, String nickname,
        List<GrantedAuthority> authorities, boolean accountNonExpired, boolean accountNonLocked,
        boolean credentialsNonExpired, boolean enabled, Function<String, String> passwordEncoder,
        LocalDateTime createdDateTime,String pictureUrl,
    String email) {
        this.url = url;
        this.uId = uId;
        this.token = token;
        this.userId = userId;
        this.password = password;
        this.username = username;
        this.authorities = authorities;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = !enabled;
        this.passwordEncoder = passwordEncoder;
        this.nickname = nickname;
        this.createdDateTime = createdDateTime;
        this.email = email;
        this.pictureUrl = pictureUrl;
    }

    @Builder
    public CustomUserDetails(Long uId,String url, String userId, String token, String password, String username,String nickname,
        boolean accountNonExpired, boolean accountNonLocked, boolean credentialsNonExpired, boolean enabled , LocalDateTime createdDateTime,String email,String pictureUrl,LocalDateTime userNameModifiedDate,Boolean isLoginBlocked) {
        this.url = url;
        this.uId = uId;
        this.token = token;
        this.userId = userId;
        this.password = this.passwordEncoder.apply(password);
        this.username = username;
        this.accountNonExpired = !accountNonExpired;
        this.accountNonLocked = !accountNonLocked;
        this.credentialsNonExpired = !credentialsNonExpired;
        this.enabled = enabled;
        this.nickname = nickname;
        this.createdDateTime = createdDateTime;
        this.email = email;
        this.pictureUrl = pictureUrl;
        this.userNameModifiedDate = userNameModifiedDate;
        this.isLoginBlocked = isLoginBlocked;
    }

    public LocalDateTime getUserNameModifiedDate() {
        return userNameModifiedDate;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }

    public boolean getLoginBlocked() {
        return isLoginBlocked;
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


    public Long getUId() {
        return uId;
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
    public  boolean isAccountNonExpired() {
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
//            ", token='" + token + '\'' +
            ", userId='" + userId + '\'' +
            ", username='" + username + '\'' +
            ", authorities=" + Arrays.toString(new List[]{authorities}) +
            ", accountNonExpired=" + accountNonExpired +
            ", accountNonLocked=" + accountNonLocked +
            ", credentialsNonExpired=" + credentialsNonExpired +
            ", enabled=" + enabled +
            '}';
    }


    @Override
    public void eraseCredentials() {
        this.password = null;
    }
}

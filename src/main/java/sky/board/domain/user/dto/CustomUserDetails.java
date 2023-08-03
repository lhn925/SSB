package sky.board.domain.user.dto;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

@Getter
@Setter
/**
 * getAuthorities() : 계정이 가지고 있는 권한 목록 리턴
 * getPassword() : 계정의 비밀번호 리턴
 * getUsername() : 계정 이름 리턴
 * isAccountNonExpired() : 계정이 만료됐는지 리턴 -> true는 완료되지 않음 의미
 * isAccountNonLocked() : 계정이 잠겨있는지 리턴 -> true는 잠기지 않음
 * isCredentialNonExpired() : 비밀번호가 만료됐는지 리턴 -> true는 만료X 의미
 * isEnabled() : 계정이 활성화돼 있는지 리턴 -> true는 활성화 상태 의미
 */

public class CustomUserDetails implements UserDetails, CredentialsContainer {


    private String password;
    private final String username;
    private String url;

    private final Set<GrantedAuthority> authorities;

    private final boolean accountNonExpired;

    private final boolean accountNonLocked;

    private final boolean credentialsNonExpired;

    private final boolean enabled;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this(username, password, true, true, true, true, authorities);
    }

    /**
     * Construct the <code>User</code> with the details required by
     * {@link org.springframework.security.authentication.dao.DaoAuthenticationProvider}.
     *
     * @param username
     *     the username presented to the
     *     <code>DaoAuthenticationProvider</code>
     * @param password
     *     the password that should be presented to the
     *     <code>DaoAuthenticationProvider</code>
     * @param enabled
     *     set to <code>true</code> if the user is enabled
     * @param accountNonExpired
     *     set to <code>true</code> if the account has not expired
     * @param credentialsNonExpired
     *     set to <code>true</code> if the credentials have not
     *     expired
     * @param accountNonLocked
     *     set to <code>true</code> if the account is not locked
     * @param authorities
     *     the authorities that should be granted to the caller if they
     *     presented the correct username and password and the user is enabled. Not null.
     * @throws IllegalArgumentException
     *     if a <code>null</code> value was passed either as
     *     a parameter or as an element in the <code>GrantedAuthority</code> collection
     */
    public CustomUserDetails(String username, String password, boolean enabled, boolean accountNonExpired,
        boolean credentialsNonExpired, boolean accountNonLocked,
        Collection<? extends GrantedAuthority> authorities) {
        Assert.isTrue(username != null && !"".equals(username) && password != null,
            "Cannot pass null or empty values to constructor");
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.authorities = Collections.unmodifiableSet(sortAuthorities(authorities));
    }

    private static SortedSet<GrantedAuthority> sortAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(authorities, "Cannot pass a null GrantedAuthority collection");
        // Ensure array iteration order is predictable (as per
        // UserDetails.getAuthorities() contract and SEC-717)
        SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<>(new AuthorityComparator());
        for (GrantedAuthority grantedAuthority : authorities) {
            Assert.notNull(grantedAuthority, "GrantedAuthority list cannot contain any null elements");
            sortedAuthorities.add(grantedAuthority);
        }
        return sortedAuthorities;
    }

    private static class AuthorityComparator implements Comparator<GrantedAuthority>, Serializable {

        private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

        @Override
        public int compare(GrantedAuthority g1, GrantedAuthority g2) {
            // Neither should ever be null as each entry is checked before adding it to
            // the set. If the authority is null, it is a custom authority and should
            // precede others.
            if (g2.getAuthority() == null) {
                return -1;
            }
            if (g1.getAuthority() == null) {
                return 1;
            }
            return g1.getAuthority().compareTo(g2.getAuthority());
        }

    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    public void eraseCredentials() {
        this.password = null;
    }

    public static CustomUserDetails.UserBuilder withUsername(String username) {
        return builder().username(username);
    }

    /**
     * Creates a UserBuilder
     *
     * @return the UserBuilder
     */
    public static CustomUserDetails.UserBuilder builder() {
        return new CustomUserDetails.UserBuilder();
    }

    public static CustomUserDetails.UserBuilder withUserDetails(UserDetails userDetails) {
        // @formatter:off
        return withUsername(userDetails.getUsername())
            .password(userDetails.getPassword())
            .accountExpired(!userDetails.isAccountNonExpired())
            .accountLocked(!userDetails.isAccountNonLocked())
            .authorities(userDetails.getAuthorities())
            .credentialsExpired(!userDetails.isCredentialsNonExpired())
            .disabled(!userDetails.isEnabled());
        // @formatter:on
    }


    @Override
    public String toString() {
        return "CustomUserDetails{" +
            "password='" + password + '\'' +
            ", username='" + username + '\'' +
            ", url='" + url + '\'' +
            ", authorities=" + authorities +
            ", accountNonExpired=" + accountNonExpired +
            ", accountNonLocked=" + accountNonLocked +
            ", credentialsNonExpired=" + credentialsNonExpired +
            ", enabled=" + enabled +
            '}';
    }

    public static final class UserBuilder {

        private String username;

        private String password;
        private String url;

        private List<GrantedAuthority> authorities;

        private boolean accountExpired;

        private boolean accountLocked;

        private boolean credentialsExpired;

        private boolean disabled;

        private Function<String, String> passwordEncoder = (password) -> password;

        private UserBuilder() {
        }

        public UserBuilder username(String username) {
            Assert.notNull(username, "username cannot be null");
            this.username = username;
            return this;
        }

        /**
         * Populates the password. This attribute is required.
         *
         * @param password
         *     the password. Cannot be null.
         * @return the {@link User.UserBuilder} for method chaining (i.e. to populate
         * additional attributes for this user)
         */
        public UserBuilder password(String password) {
            Assert.notNull(password, "password cannot be null");
            this.password = password;
            return this;
        }

        /**
         * Encodes the current password (if non-null) and any future passwords supplied to
         * {@link #password(String)}.
         *
         * @param encoder
         *     the encoder to use
         * @return the {@link User.UserBuilder} for method chaining (i.e. to populate
         * additional attributes for this user)
         */
        public UserBuilder passwordEncoder(Function<String, String> encoder) {
            Assert.notNull(encoder, "encoder cannot be null");
            this.passwordEncoder = encoder;
            return this;
        }

        /**
         * Populates the roles. This method is a shortcut for calling
         * {@link #authorities(String...)}, but automatically prefixes each entry with
         * "ROLE_". This means the following:
         *
         * <code>
         * builder.roles("USER","ADMIN");
         * </code>
         * <p>
         * is equivalent to
         *
         * <code>
         * builder.authorities("ROLE_USER","ROLE_ADMIN");
         * </code>
         *
         * <p>
         * This attribute is required, but can also be populated with
         * {@link #authorities(String...)}.
         * </p>
         *
         * @param roles
         *     the roles for this user (i.e. USER, ADMIN, etc). Cannot be null,
         *     contain null values or start with "ROLE_"
         * @return the {@link User.UserBuilder} for method chaining (i.e. to populate
         * additional attributes for this user)
         */
        public UserBuilder roles(String... roles) {
            List<GrantedAuthority> authorities = new ArrayList<>(roles.length);
            for (String role : roles) {
                Assert.isTrue(!role.startsWith("ROLE_"),
                    () -> role + " cannot start with ROLE_ (it is automatically added)");
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
            return authorities(authorities);
        }

        /**
         * Populates the authorities. This attribute is required.
         *
         * @param authorities
         *     the authorities for this user. Cannot be null, or contain
         *     null values
         * @return the {@link User.UserBuilder} for method chaining (i.e. to populate
         * additional attributes for this user)
         * @see #roles(String...)
         */
        public UserBuilder authorities(GrantedAuthority... authorities) {
            return authorities(Arrays.asList(authorities));
        }

        /**
         * Populates the authorities. This attribute is required.
         *
         * @param authorities
         *     the authorities for this user. Cannot be null, or contain
         *     null values
         * @return the {@link User.UserBuilder} for method chaining (i.e. to populate
         * additional attributes for this user)
         * @see #roles(String...)
         */
        public UserBuilder authorities(Collection<? extends GrantedAuthority> authorities) {
            this.authorities = new ArrayList<>(authorities);
            return this;
        }

        /**
         * Populates the authorities. This attribute is required.
         *
         * @param authorities
         *     the authorities for this user (i.e. ROLE_USER, ROLE_ADMIN,
         *     etc). Cannot be null, or contain null values
         * @return the {@link User.UserBuilder} for method chaining (i.e. to populate
         * additional attributes for this user)
         * @see #roles(String...)
         */
        public UserBuilder authorities(String... authorities) {
            return authorities(AuthorityUtils.createAuthorityList(authorities));
        }

        /**
         * Defines if the account is expired or not. Default is false.
         *
         * @param accountExpired
         *     true if the account is expired, false otherwise
         * @return the {@link User.UserBuilder} for method chaining (i.e. to populate
         * additional attributes for this user)
         */
        public UserBuilder accountExpired(boolean accountExpired) {
            this.accountExpired = accountExpired;
            return this;
        }

        public UserBuilder url(String url) {
            this.url = url;
            return this;
        }


        /**
         * Defines if the account is locked or not. Default is false.
         *
         * @param accountLocked
         *     true if the account is locked, false otherwise
         * @return the {@link User.UserBuilder} for method chaining (i.e. to populate
         * additional attributes for this user)
         */
        public UserBuilder accountLocked(boolean accountLocked) {
            this.accountLocked = accountLocked;
            return this;
        }

        /**
         * Defines if the credentials are expired or not. Default is false.
         *
         * @param credentialsExpired
         *     true if the credentials are expired, false otherwise
         * @return the {@link User.UserBuilder} for method chaining (i.e. to populate
         * additional attributes for this user)
         */
        public UserBuilder credentialsExpired(boolean credentialsExpired) {
            this.credentialsExpired = credentialsExpired;
            return this;
        }

        /**
         * Defines if the account is disabled or not. Default is false.
         *
         * @param disabled
         *     true if the account is disabled, false otherwise
         * @return the {@link User.UserBuilder} for method chaining (i.e. to populate
         * additional attributes for this user)
         */
        public UserBuilder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public UserDetails build() {
            String encodedPassword = this.passwordEncoder.apply(this.password);
            return new CustomUserDetails(this.username, encodedPassword, !this.disabled, !this.accountExpired,
                !this.credentialsExpired, !this.accountLocked, this.authorities);
        }

    }


}

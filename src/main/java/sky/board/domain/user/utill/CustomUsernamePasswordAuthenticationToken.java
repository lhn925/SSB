package sky.board.domain.user.utill;

import java.util.Collection;
import java.util.List;
import javax.security.auth.login.LoginContext;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.jaas.JaasAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;


@Getter
@Setter
public class CustomUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private Object url;

    public CustomUsernamePasswordAuthenticationToken(Object url ,Object principal, Object credentials) {
        super(principal, credentials);
        this.url = url;
    }
    public CustomUsernamePasswordAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
        this.url = url;

    }
    public CustomUsernamePasswordAuthenticationToken(Object principal, Object credentials,
        Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }


    public Object getUrl() {
        return url;
    }
    public void setUrl(Object url) {
        this.url = url;
    }

    public static CustomUsernamePasswordAuthenticationToken unauthenticated(Object url,Object principal, Object credentials) {
        return new CustomUsernamePasswordAuthenticationToken(url,principal, credentials);
    }



}

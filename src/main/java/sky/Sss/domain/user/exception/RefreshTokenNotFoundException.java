package sky.Sss.domain.user.exception;

import org.springframework.security.core.AuthenticationException;

public class RefreshTokenNotFoundException extends AuthenticationException {

    public RefreshTokenNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public RefreshTokenNotFoundException(String msg) {
        super(msg);
    }
    public RefreshTokenNotFoundException() {
        super(null);
    }
}

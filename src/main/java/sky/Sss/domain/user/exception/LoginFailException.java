package sky.Sss.domain.user.exception;

import org.springframework.security.core.AuthenticationException;

public class LoginFailException extends AuthenticationException {

    public LoginFailException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public LoginFailException(String msg) {
        super(msg);
    }
}

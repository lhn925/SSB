package sky.board.domain.user.exception;

import org.springframework.security.core.AuthenticationException;

public class LoginFailCountException extends AuthenticationException {


    public LoginFailCountException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public LoginFailCountException(String msg) {
        super(msg);
    }
}

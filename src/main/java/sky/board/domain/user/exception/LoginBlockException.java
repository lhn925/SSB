package sky.board.domain.user.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 해외 로그인 차단
 */
public class LoginBlockException extends AuthenticationException {


    public LoginBlockException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public LoginBlockException(String msg) {
        super(msg);
    }
}

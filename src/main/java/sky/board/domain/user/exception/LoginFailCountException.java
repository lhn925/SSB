package sky.board.domain.user.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 로그인 실패 횟수 초과 예외
 */
public class LoginFailCountException extends AuthenticationException {


    public LoginFailCountException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public LoginFailCountException(String msg) {
        super(msg);
    }
}

package sky.Sss.domain.user.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * MissingCapthcahFactorException : 2차 인증 번호를 입력하지 않음
 */
public class MissingCapthcahFactorException extends IllegalArgumentException {


    public MissingCapthcahFactorException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public MissingCapthcahFactorException(String msg) {
        super(msg);
    }
}

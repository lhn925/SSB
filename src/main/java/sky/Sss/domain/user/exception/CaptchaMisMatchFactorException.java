package sky.Sss.domain.user.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * MissingCapthcahFactorException : 2차 인증 번호를 입력하지 않음
 */
public class CaptchaMisMatchFactorException extends AuthenticationException {


    public CaptchaMisMatchFactorException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CaptchaMisMatchFactorException(String msg) {
        super(msg);
    }
}

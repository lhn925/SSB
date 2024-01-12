package sky.Sss.domain.track.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

/**
 * 파일 접근 제한시
 */
@Getter
public class SsbTrackAccessDeniedException extends AccessDeniedException {


    private HttpStatus httpStatus;
    private String code;
    public SsbTrackAccessDeniedException(String msg) {
        super(msg);
        this.httpStatus = HttpStatus.FORBIDDEN;
        this.code = msg;
    }

    public SsbTrackAccessDeniedException(String msg,HttpStatus httpStatus) {
        super(msg);
        this.code = msg;
        this.httpStatus = httpStatus;
    }

    public SsbTrackAccessDeniedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

package sky.Sss.domain.track.exception.checked;

import org.springframework.http.HttpStatus;
import sky.Sss.domain.track.exception.checked.SsbFileException;

public class SsbFileLengthLimitOverException extends SsbFileException {

    public SsbFileLengthLimitOverException(HttpStatus httpStatus, String code) {
        super(httpStatus, code);
    }

    public SsbFileLengthLimitOverException() {
        super(HttpStatus.BAD_REQUEST, "track.error.limit");
    }
}

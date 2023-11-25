package sky.Sss.domain.track.exception;

import org.springframework.http.HttpStatus;

public class SsbFileLengthLimitOverException extends SsbFileException {

    public SsbFileLengthLimitOverException(HttpStatus httpStatus, String code) {
        super(httpStatus, code);
    }

    public SsbFileLengthLimitOverException() {
        super(HttpStatus.BAD_REQUEST, "track.error.limit");
    }
}

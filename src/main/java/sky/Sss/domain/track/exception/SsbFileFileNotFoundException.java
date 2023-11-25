package sky.Sss.domain.track.exception;


import org.springframework.http.HttpStatus;

public class SsbFileFileNotFoundException extends SsbFileException {

    public SsbFileFileNotFoundException(HttpStatus httpStatus, String code) {
        super(httpStatus, code);
    }

    public SsbFileFileNotFoundException() {
        super(HttpStatus.NOT_FOUND, "file.error.notFind");
    }
}

package sky.Sss.domain.track.exception;


import org.springframework.http.HttpStatus;

public class SsbFileNotFoundException extends SsbFileException {

    public SsbFileNotFoundException(HttpStatus httpStatus, String code) {
        super(httpStatus, code);
    }
    public SsbFileNotFoundException(HttpStatus httpStatus) {
        super(httpStatus,  "file.error.notFind");
    }


    public SsbFileNotFoundException() {
        super(HttpStatus.NOT_FOUND, "file.error.notFind");
    }
}

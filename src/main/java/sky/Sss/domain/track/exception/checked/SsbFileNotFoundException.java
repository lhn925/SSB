package sky.Sss.domain.track.exception.checked;


import org.springframework.http.HttpStatus;
import sky.Sss.domain.track.exception.checked.SsbFileException;

public class SsbFileNotFoundException extends SsbFileException {

    public SsbFileNotFoundException(HttpStatus httpStatus, String code) {
        super(httpStatus, code);
    }
    public SsbFileNotFoundException(HttpStatus httpStatus) {
        super(httpStatus,  "file.error.notFind");
    }


    public SsbFileNotFoundException() {
        super(HttpStatus.FORBIDDEN, "file.error.notFind");
    }
}

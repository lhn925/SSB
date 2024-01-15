package sky.Sss.global.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;
import sky.Sss.domain.track.exception.checked.SsbFileException;

@Getter
public class FileExtConstraintException extends SsbFileException {

    public FileExtConstraintException(String code) {
        super(HttpStatus.BAD_REQUEST, code);
    }
    public FileExtConstraintException() {
        super(HttpStatus.BAD_REQUEST, "track.ext.error");
    }
}

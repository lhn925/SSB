package sky.Sss.domain.track.exception.checked;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SsbFileException extends RuntimeException{

    private HttpStatus httpStatus;
    private String code;

    public SsbFileException(HttpStatus httpStatus,String code) {
        this.httpStatus = httpStatus;
        this.code = code;
    }

    public SsbFileException() {
        super();
    }

}

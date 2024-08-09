package sky.Sss.global.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NO_CONTENT)
public class NoContentException extends RuntimeException{
    public NoContentException() {
        super("No content available.");
    }

    public NoContentException(String message) {
        super(message);
    }
}

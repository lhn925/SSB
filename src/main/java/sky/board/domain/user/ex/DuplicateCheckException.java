package sky.board.domain.user.ex;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class DuplicateCheckException extends RuntimeException{

    public DuplicateCheckException(String message) {
        super(message);
    }
}

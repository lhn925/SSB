package sky.board.domain.user.ex;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class UserJoinServerErrorException extends RuntimeException{

    public UserJoinServerErrorException(String message) {
        super(message);
    }
}

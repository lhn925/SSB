package sky.board.domain.user.exception;


public class UserJoinServerErrorException extends RuntimeException{

    public UserJoinServerErrorException(String message) {
        super(message);
    }
}

package sky.Sss.domain.user.exception;

/**
 * 유저 회원 가입시 Exception
 */
public class UserJoinServerErrorException extends RuntimeException{

    public UserJoinServerErrorException(String message) {
        super(message);
    }
}

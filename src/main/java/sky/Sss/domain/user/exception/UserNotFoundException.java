package sky.Sss.domain.user.exception;

/**
 * 유저를 찾을수 없을 때
 */
public class UserNotFoundException extends IllegalArgumentException {

    private String message;

    public UserNotFoundException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return message;
    }

}

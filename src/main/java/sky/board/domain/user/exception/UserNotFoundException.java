package sky.board.domain.user.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserNotFoundException extends UsernameNotFoundException {

    private String message;

    public UserNotFoundException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return message;
    }

}

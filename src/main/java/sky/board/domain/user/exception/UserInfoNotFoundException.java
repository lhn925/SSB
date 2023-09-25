package sky.board.domain.user.exception;

import lombok.Getter;

@Getter
public class UserInfoNotFoundException extends RuntimeException{


    public UserInfoNotFoundException(String message) {
        super(message);
    }

    public UserInfoNotFoundException() {
    }
}

package sky.Sss.domain.user.exception;

import lombok.Getter;

@Getter
public class ChangeUserNameIsNotAfterException extends RuntimeException{


    public ChangeUserNameIsNotAfterException(String message) {
        super(message);
    }
}

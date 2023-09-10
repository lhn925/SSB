package sky.board.domain.user.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 중복값 체크 예외
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class DuplicateCheckException extends RuntimeException {

    private String message;
    private String fieldName;
    private String rejectValue;

    public DuplicateCheckException(String message,String fieldName) {
        this.message = message;
        this.fieldName = fieldName;
    }

    public DuplicateCheckException(String message,String fieldName,String rejectValue) {
        this.message = message;
        this.fieldName = fieldName;
        this.rejectValue = rejectValue;
    }

    public DuplicateCheckException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getRejectValue() {
        return rejectValue;
    }
}

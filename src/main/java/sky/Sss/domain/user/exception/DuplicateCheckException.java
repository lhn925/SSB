package sky.Sss.domain.user.exception;


import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 중복값 체크 예외
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class DuplicateCheckException extends BindException {

    private String message;
    private String fieldName;
    private String rejectValue;

    public DuplicateCheckException(BindingResult bindingResult, String message,
        String fieldName, String rejectValue) {
        super(bindingResult);
        this.message = message;
        this.fieldName = fieldName;
        this.rejectValue = rejectValue;
    }

    public DuplicateCheckException(Object target, String objectName, String message, String fieldName,
        String rejectValue) {
        super(target, objectName);
        this.message = message;
        this.fieldName = fieldName;
        this.rejectValue = rejectValue;
    }

    public DuplicateCheckException(BindingResult bindingResult) {
        super(bindingResult);
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

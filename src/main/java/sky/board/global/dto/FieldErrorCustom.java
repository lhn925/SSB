package sky.board.global.dto;

import org.springframework.validation.FieldError;

public class FieldErrorCustom extends FieldError {

    public FieldErrorCustom(String objectName, String field, String defaultMessage) {
        super(objectName, field, defaultMessage);
    }

    public FieldErrorCustom(String objectName, String field, Object rejectedValue, boolean bindingFailure,
        String[] codes,
        Object[] arguments, String defaultMessage) {
        super(objectName, field, rejectedValue, bindingFailure, codes, arguments, defaultMessage);
    }


    // custom
    public FieldErrorCustom(String objectName, String field, Object rejectedValue, String code, String[] arguments) {
        super(objectName, field, rejectedValue, false, new String[]{code}, arguments, null);
    }
}

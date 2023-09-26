package sky.Sss.global.error.dto;

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


    /**
     *
     * @param objectName dto
     * @param field input에 아이디또는 name
     * @param rejectedValue 반환값
     * @param code 문자코드
     * @param arguments 문자코드에 삽입할 값
     */
    // custom
    public FieldErrorCustom(String objectName, String field, Object rejectedValue, String code, String[] arguments) {
        super(objectName, field, rejectedValue, false, new String[]{code}, arguments, null);
    }
}

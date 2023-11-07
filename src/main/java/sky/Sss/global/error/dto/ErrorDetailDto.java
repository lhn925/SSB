package sky.Sss.global.error.dto;

import java.util.Locale;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.validation.FieldError;

@Getter
public class ErrorDetailDto extends ErrorDetail {

    private String field;
    private Object rejectValue;
    public ErrorDetailDto(FieldError fieldError, MessageSource ms, Locale locale) {
        super(fieldError.getObjectName(), fieldError.getCode(), ms.getMessage(fieldError, locale));
        this.field = fieldError.getField();
        if (fieldError.getRejectedValue() != null) {
            this.rejectValue = fieldError.getRejectedValue();
        }
    }

    public ErrorDetailDto(String field, String code, MessageSource ms, Locale locale) {
        super(code, ms.getMessage(code, null, locale));
        this.field = field;
    }

    public ErrorDetailDto(String field, String code, MessageSource ms, Locale locale, Object[] args) {
        super(code, ms.getMessage(code, args, locale));
        this.field = field;
    }

}

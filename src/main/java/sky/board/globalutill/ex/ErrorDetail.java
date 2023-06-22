package sky.board.globalutill.ex;

import java.util.Locale;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.validation.FieldError;

@Getter
public class ErrorDetail {

    private String objectName;
    private String field;
    private String code;
    private String message;

    @Builder
    public ErrorDetail(FieldError fieldError, MessageSource messageSource, Locale locale) {
        this.objectName = fieldError.getObjectName();
        this.field = fieldError.getField();
        this.code = fieldError.getCode();
        this.message = messageSource.getMessage(fieldError, locale);
    }

}

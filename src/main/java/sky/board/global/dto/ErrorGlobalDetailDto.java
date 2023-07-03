package sky.board.global.dto;

import java.util.Locale;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

@Getter
public class ErrorGlobalDetailDto extends ErrorDetail {
    public ErrorGlobalDetailDto(ObjectError objectError, MessageSource ms, Locale locale) {
        super(objectError.getObjectName(), objectError.getCode(),
            ms.getMessage(objectError, locale));
    }
}

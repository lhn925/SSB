package sky.Sss.global.error.dto;

import java.util.Locale;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.validation.ObjectError;

@Getter
public class ErrorGlobalDetailDto extends ErrorDetail {

    public ErrorGlobalDetailDto(ObjectError objectError, MessageSource ms, Locale locale) {
        super(objectError.getObjectName(), objectError.getCode(),
            ms.getMessage(objectError, locale));
    }


    public ErrorGlobalDetailDto(String code, MessageSource ms, Locale locale) {
        super(code, ms.getMessage(code, null, locale));
    }
    public ErrorGlobalDetailDto(String code, MessageSource ms, Locale locale,Object[] args) {
        super(code, ms.getMessage(code, args, locale));
    }
}

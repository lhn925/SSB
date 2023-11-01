package sky.Sss.global.error.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;

@Getter
@NoArgsConstructor
public class ErrorGlobalResultDto implements ErrorResult {

    private List<ErrorGlobalDetailDto> errorDetails;

    public ErrorGlobalResultDto(Errors errors, MessageSource messageSource, Locale locale) {
        errorDetails = errors.getGlobalErrors()
            .stream()
            .map(error -> new ErrorGlobalDetailDto(error, messageSource, locale))
            .toList();
    }

    public ErrorGlobalResultDto(String code, MessageSource messageSource, Locale locale) {
        this.errorDetails = new ArrayList<>();
        errorDetails.add(new ErrorGlobalDetailDto(code, messageSource, locale));
    }

    public ErrorGlobalResultDto(String code, MessageSource messageSource, Locale locale, Object[] args) {
        this.errorDetails = new ArrayList<>();
        errorDetails.add(new ErrorGlobalDetailDto(code, messageSource, locale, args));
    }



}

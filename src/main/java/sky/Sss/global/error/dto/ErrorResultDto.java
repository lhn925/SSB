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
public class ErrorResultDto implements ErrorResult {

    private List<ErrorDetailDto> errorDetails;

    public ErrorResultDto(Errors errors, MessageSource messageSource, Locale locale) {
        errorDetails = errors.getFieldErrors()
            .stream()
            .map(error -> new ErrorDetailDto(error, messageSource, locale))
            .toList();
    }

    public ErrorResultDto(String field, String code, MessageSource messageSource, Locale locale) {
        this.errorDetails = new ArrayList<>();
        errorDetails.add(new ErrorDetailDto(field, code, messageSource, locale));
    }


    public ErrorResultDto(String field, String code, MessageSource messageSource, Locale locale, Object[] args) {
        this.errorDetails = new ArrayList<>();
        errorDetails.add(new ErrorDetailDto(field, code, messageSource, locale, args));
    }


}

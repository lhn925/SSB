package sky.board.globalutill.ex;

import java.util.List;
import java.util.Locale;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;

@Getter
@NoArgsConstructor
public class ErrorResult {

    private List<ErrorDetail> errorDetails;

    @Builder
    public ErrorResult(Errors errors, MessageSource messageSource, Locale locale) {
        errorDetails = errors.getFieldErrors()
            .stream()
            .map(error -> ErrorDetail.builder()
                .fieldError(error)
                .messageSource(messageSource)
                .locale(locale)
                .build()
            ).toList();
    }
}

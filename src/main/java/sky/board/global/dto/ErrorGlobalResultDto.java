package sky.board.global.dto;

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

    private List<ErrorGlobalDetailDto> globalDetails;

    public ErrorGlobalResultDto(Errors errors, MessageSource messageSource, Locale locale) {
        globalDetails = errors.getGlobalErrors()
            .stream()
            .map(error -> new ErrorGlobalDetailDto(error, messageSource, locale))
            .toList();
    }

}

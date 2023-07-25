package sky.board.global.dto;

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

//    public static ResponseEntity<ErrorResultDto> getErrorResultDtoResponseEntity(
//        BindingResult bindingResult, MessageSource ms, HttpServletRequest request) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResultDto.builder()
//            .errors(bindingResult)
//            .messageSource(ms)
//            .locale(request.getLocale()).build());
//    }
}

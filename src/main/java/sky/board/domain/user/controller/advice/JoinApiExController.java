package sky.board.domain.user.controller.advice;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sky.board.domain.user.api.join.JoinApiController;
import sky.board.domain.user.exception.DuplicateCheckException;
import sky.board.global.error.dto.ErrorResultDto;
import sky.board.global.error.dto.FieldErrorCustom;
import sky.board.global.error.dto.Result;

@RestControllerAdvice(assignableTypes = {JoinApiController.class})
@RequiredArgsConstructor
public class JoinApiExController {

    private final MessageSource ms;

    @ExceptionHandler({DuplicateCheckException.class})
    public ResponseEntity duplicateCheckExHandle (DuplicateCheckException e ,HttpServletRequest request) {
        BindingResult bindingResult = (BindingResult) request.getAttribute("bindingResult");
        bindingResult.addError(
            new FieldErrorCustom(
                "JoinDuplicateDto",
                e.getFieldName(), e.getRejectValue(),
                "duplication",
                new String[]{e.getMessage()}));
        return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
    }
}

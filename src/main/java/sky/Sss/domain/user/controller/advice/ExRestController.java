package sky.Sss.domain.user.controller.advice;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sky.Sss.domain.user.exception.DuplicateCheckException;
import sky.Sss.domain.user.exception.UserInfoNotFoundException;
import sky.Sss.global.error.dto.ErrorGlobalResultDto;
import sky.Sss.global.error.dto.ErrorResultDto;
import sky.Sss.global.error.dto.FieldErrorCustom;
import sky.Sss.global.error.dto.Result;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExRestController {

    private final MessageSource ms;

    @ExceptionHandler({DuplicateCheckException.class})
    public ResponseEntity duplicateCheckExHandle(DuplicateCheckException e,
        HttpServletRequest request) {
        BindingResult bindingResult = e.getBindingResult();
                    bindingResult.addError(
                new FieldErrorCustom(
                    "userJoinPostDto",
                    e.getFieldName(),
                    e.getRejectValue(),
                    "duplication",
                    new String[]{e.getMessage()}));
        return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
    }

    @ExceptionHandler({UserInfoNotFoundException.class, UsernameNotFoundException.class})
    public ResponseEntity helpExHandle(RuntimeException e, HttpServletRequest request) {
        String errorCode = null;
        try {
            ms.getMessage(e.getMessage(), null, request.getLocale());
            errorCode = e.getMessage();
        } catch (NoSuchMessageException ex) {
            errorCode = "error";
        }
        return Result.getErrorResult(new ErrorGlobalResultDto(errorCode, ms, request.getLocale()));
    }
}

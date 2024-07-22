package sky.Sss.domain.user.controller.advice;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sky.Sss.domain.user.exception.ChangeUserNameIsNotAfterException;
import sky.Sss.domain.user.exception.DuplicateCheckException;
import sky.Sss.domain.user.exception.RefreshTokenNotFoundException;
import sky.Sss.domain.user.exception.UserInfoNotFoundException;
import sky.Sss.global.error.dto.ErrorGlobalResultDto;
import sky.Sss.global.error.dto.ErrorResult;
import sky.Sss.global.error.dto.ErrorResultDto;
import sky.Sss.global.error.dto.FieldErrorCustom;
import sky.Sss.global.error.dto.Result;

@Slf4j
@RestControllerAdvice(basePackages = {"sky.Sss.domain.user"})
@RequiredArgsConstructor
public class ExUserController {

    private final MessageSource ms;

    @ExceptionHandler({DuplicateCheckException.class})
    public ResponseEntity<ErrorResult> duplicateCheckExHandle(DuplicateCheckException e,
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

    @ExceptionHandler({UserInfoNotFoundException.class, UsernameNotFoundException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResult> helpExHandle(RuntimeException e, HttpServletRequest request) {
        String errorCode = null;
        try {
            ms.getMessage(e.getMessage(), null, request.getLocale());
            errorCode = e.getMessage();
        } catch (NoSuchMessageException ex) {
            errorCode = "error";
        }
        return Result.getErrorResult(new ErrorGlobalResultDto(errorCode, ms, request.getLocale()));
    }
    /**
     *
     * @return
     */
    @ExceptionHandler({ChangeUserNameIsNotAfterException.class})
    public ResponseEntity<?> ChangeIsNotAfterExHandle(ChangeUserNameIsNotAfterException e, HttpServletRequest request) {

        return getErrorResultResponseEntity("change.isNotAfter", request, e.getMessage());
    }


    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<ErrorGlobalResultDto> accessExHandler(AccessDeniedException e, HttpServletRequest request) {
        return new ResponseEntity<>(new ErrorGlobalResultDto("access.error.forbidden", ms, request.getLocale()),
            HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<ErrorGlobalResultDto> authExHandler(AuthenticationException e, HttpServletRequest request) {
        return new ResponseEntity<>(new ErrorGlobalResultDto("token.error", ms, request.getLocale()),
            HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({RefreshTokenNotFoundException.class})
    public ResponseEntity<ErrorGlobalResultDto> refreshExHandler(RefreshTokenNotFoundException e, HttpServletRequest request) {
        return new ResponseEntity<>(new ErrorGlobalResultDto(e.getMessage(), ms, request.getLocale()),
            HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<ErrorResult> ioExHandler(RuntimeException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        log.info("e.getMessage() = {}", e.getMessage());
        if (e.getClass().equals(IllegalArgumentException.class)) {
            status = HttpStatus.BAD_REQUEST;
        }
        try {
            ErrorGlobalResultDto errorGlobalResultDto = new ErrorGlobalResultDto(e.getMessage(), ms,
                request.getLocale());
            return Result.getErrorResult(errorGlobalResultDto, status);
        } catch (NoSuchMessageException ex) {
            return Result.getErrorResult(new ErrorGlobalResultDto("error", ms, request.getLocale()), status);
        }
    }

    private ResponseEntity<ErrorResult> getErrorResultResponseEntity(String e, HttpServletRequest request,
        HttpStatus badRequest) {
        ErrorGlobalResultDto errorGlobalResultDto = new ErrorGlobalResultDto(e, ms, request.getLocale());
        return Result.getErrorResult(errorGlobalResultDto, badRequest);
    }

    private ResponseEntity<ErrorResult> getErrorResultResponseEntity(String code, HttpServletRequest request,
        String e) {
        ErrorGlobalResultDto errorGlobalResultDto = new ErrorGlobalResultDto(code, ms, request.getLocale(),
            new String[]{e});
        return Result.getErrorResult(errorGlobalResultDto);
    }



}

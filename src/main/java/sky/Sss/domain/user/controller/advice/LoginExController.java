package sky.Sss.domain.user.controller.advice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sky.Sss.domain.user.controller.UserLoginController;
import sky.Sss.domain.user.exception.UserInfoNotFoundException;
import sky.Sss.global.error.dto.ErrorGlobalResultDto;
import sky.Sss.global.error.dto.ErrorResult;

@Slf4j
//@RestControllerAdvice(assignableTypes = {UserLoginController.class})
@RequiredArgsConstructor
public class LoginExController {

    private final MessageSource ms;
//
//    @ExceptionHandler({AuthenticationException.class, UserInfoNotFoundException.class, IllegalArgumentException.class,
//        BadCredentialsException.class})
//    public ResponseEntity<ErrorResult> loginExHandler(RuntimeException ex, HttpServletRequest request) {
//
//        String code = ex.getMessage();
//
//        log.info("LoginExController");
//
//        try {
//            ms.getMessage(code, null, request.getLocale());
//        } catch (NoSuchMessageException e) {
//            code = "login.error";
//        }
//        return new ResponseEntity<>(new ErrorGlobalResultDto(code, ms, request.getLocale()),
//            HttpStatus.BAD_REQUEST);
//    }
}

package sky.Sss.global;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sky.Sss.global.error.dto.ErrorGlobalResultDto;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ExGlobalController {

    private final MessageSource ms;
    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity accessExHandler (AccessDeniedException e,HttpServletRequest request) {
        return new ResponseEntity(new ErrorGlobalResultDto("access.error.forbidden", ms, request.getLocale()),
            HttpStatus.FORBIDDEN);
    }

}

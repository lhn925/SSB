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
import sky.Sss.domain.track.exception.SsbTrackAccessDeniedException;
import sky.Sss.global.error.dto.ErrorGlobalResultDto;
import sky.Sss.global.error.dto.ErrorResult;
import sky.Sss.global.error.dto.Result;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice(basePackages = {"sky.Sss"})
public class ExGlobalController {

    private final MessageSource ms;
    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<ErrorResult> accessExHandler (AccessDeniedException e,HttpServletRequest request) {
        return new ResponseEntity(new ErrorGlobalResultDto("access.error.forbidden", ms, request.getLocale()),
            HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler({SsbTrackAccessDeniedException.class})
    public ResponseEntity<ErrorResult> trackAccessExHandler(SsbTrackAccessDeniedException e, HttpServletRequest request) {
        log.info("e.getMessage() = {}", e.getMessage());
        return Result.getErrorResult(new ErrorGlobalResultDto(e.getMessage(), ms, request.getLocale()),
            HttpStatus.FORBIDDEN);
    }
}

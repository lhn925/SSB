package sky.Sss.domain.track.controller.advice;


import jakarta.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sky.Sss.domain.track.exception.SsbFileException;
import sky.Sss.global.error.dto.ErrorGlobalResultDto;
import sky.Sss.global.error.dto.Result;

@Slf4j
@RestControllerAdvice(basePackages = {"sky.Sss.domain.track"})
@RequiredArgsConstructor
public class ExTrackController {

    private final MessageSource ms;


    @ExceptionHandler({SsbFileException.class})
    public ResponseEntity fileExHandler(SsbFileException e, HttpServletRequest request) {
        return Result.getErrorResult(new ErrorGlobalResultDto(e.getCode(), ms, request.getLocale()), e.getHttpStatus());
    }
    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity ioExHandler(RuntimeException e, HttpServletRequest request) {
        return Result.getErrorResult(new ErrorGlobalResultDto("error", ms, request.getLocale()),HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

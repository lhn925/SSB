package sky.Sss.domain.track.controller.advice;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sky.Sss.domain.track.exception.checked.SsbFileException;
import sky.Sss.domain.track.exception.checked.SsbPlayIncompleteException;
import sky.Sss.domain.track.exception.checked.SsbTrackAccessDeniedException;
import sky.Sss.global.error.dto.ErrorGlobalResultDto;
import sky.Sss.global.error.dto.ErrorResult;
import sky.Sss.global.error.dto.Result;
import sky.Sss.global.exception.NoContentException;

@Slf4j
@RestControllerAdvice(basePackages = {"sky.Sss.domain.track"})
@RequiredArgsConstructor
public class ExTrackController {

    private final MessageSource ms;


    @ExceptionHandler({SsbFileException.class})
    public ResponseEntity<ErrorResult> fileExHandler(SsbFileException e, HttpServletRequest request) {
        return Result.getErrorResult(new ErrorGlobalResultDto(e.getCode(), ms, request.getLocale()), e.getHttpStatus());
    }

    @ExceptionHandler({SsbTrackAccessDeniedException.class})
    public ResponseEntity<ErrorResult> fileAccessExHandler(SsbTrackAccessDeniedException e,
        HttpServletRequest request) {
        return Result.getErrorResult(new ErrorGlobalResultDto(e.getCode(), ms, request.getLocale()), e.getHttpStatus());
    }

    // track error 422
    @ExceptionHandler({SsbPlayIncompleteException.class})
    public ResponseEntity<ErrorResult> playIncompleteExHandler(SsbPlayIncompleteException e,
        HttpServletRequest request) {
        return Result.getErrorResult(new ErrorGlobalResultDto("error", ms, request.getLocale()),
            HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<ErrorResult> ioExHandler(RuntimeException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (e.getClass().equals(IllegalArgumentException.class)) {
            status = HttpStatus.BAD_REQUEST;
        } else if (e.getClass().equals(NoContentException.class)) {
            status = HttpStatus.NO_CONTENT;
        }
        try {
            ErrorGlobalResultDto errorGlobalResultDto = new ErrorGlobalResultDto(e.getMessage(), ms,
                request.getLocale());
            return Result.getErrorResult(errorGlobalResultDto, status);
        } catch (Exception ex) {
            return Result.getErrorResult(new ErrorGlobalResultDto("error", ms, request.getLocale()), status);
        }

    }
}

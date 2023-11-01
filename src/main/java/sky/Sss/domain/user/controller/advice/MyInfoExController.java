package sky.Sss.domain.user.controller.advice;


import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sky.Sss.domain.user.controller.MyInfoController;
import sky.Sss.domain.user.exception.ChangeUserNameIsNotAfterException;
import sky.Sss.domain.user.exception.DuplicateCheckException;
import sky.Sss.domain.user.exception.UserInfoNotFoundException;
import sky.Sss.global.error.dto.ErrorGlobalResultDto;
import sky.Sss.global.error.dto.ErrorResult;
import sky.Sss.global.error.dto.Result;

@Slf4j
@RestControllerAdvice(assignableTypes = {MyInfoController.class})
@RequiredArgsConstructor
public class MyInfoExController {

    private final MessageSource ms;

    /**
     * RestController 이용중 로그인이 안됐을 경우
     *
     * @return
     */
    @ExceptionHandler({UserInfoNotFoundException.class})
    public ResponseEntity userInfoExHandle(UserInfoNotFoundException e, HttpServletRequest request) {
        return getErrorResultResponseEntity(e.getMessage(), request, HttpStatus.FORBIDDEN);
    }

    /**
     * 중복 체크 (닉네임,이메일,아이디)
     *
     * @return
     */
    @ExceptionHandler({DuplicateCheckException.class})
    public ResponseEntity duplicateCheckExHandle(DuplicateCheckException e, HttpServletRequest request) {
        return getErrorResultResponseEntity("duplication", request, e.getMessage());
    }

    /**
     *
     * @return
     */
    @ExceptionHandler({ChangeUserNameIsNotAfterException.class})
    public ResponseEntity ChangeIsNotAfterExHandle(ChangeUserNameIsNotAfterException e, HttpServletRequest request) {
        return getErrorResultResponseEntity("change.isNotAfter", request, e.getMessage());
    }

    /**
     * 중복 체크 (닉네임,이메일,아이디)
     *
     * @return
     */
    @ExceptionHandler({IOException.class, RuntimeException.class, IllegalArgumentException.class})
    public ResponseEntity IoExHandle(Exception e, HttpServletRequest request) {

        ResponseEntity<ErrorResult> errorResultResponseEntity = null;
        try {
            errorResultResponseEntity = getErrorResultResponseEntity(e.getMessage(),
                request, HttpStatus.BAD_REQUEST);
        } catch (NoSuchMessageException ex) {
            errorResultResponseEntity = getErrorResultResponseEntity("error",
                request, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return errorResultResponseEntity;
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

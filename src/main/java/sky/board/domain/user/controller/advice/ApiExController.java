package sky.board.domain.user.controller.advice;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sky.board.domain.user.api.myInfo.MyInfoApiController;
import sky.board.domain.user.exception.ChangeUserNameIsNotAfterException;
import sky.board.domain.user.exception.DuplicateCheckException;
import sky.board.domain.user.exception.UserInfoNotFoundException;
import sky.board.global.error.dto.ErrorGlobalResultDto;
import sky.board.global.error.dto.ErrorResult;
import sky.board.global.error.dto.Result;

@Slf4j
@RestControllerAdvice(assignableTypes = {MyInfoApiController.class})
@RequiredArgsConstructor
public class ApiExController {

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
     * 중복 체크 (닉네임,이메일,아이디)
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
        return getErrorResultResponseEntity(e.getMessage(), request, HttpStatus.BAD_REQUEST);
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

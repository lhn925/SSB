package sky.board.domain.user.controller.advice;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import sky.board.domain.user.api.myInfo.MyInfoApiController;
import sky.board.domain.user.exception.UserInfoNotFoundException;
import sky.board.global.utili.Alert;

@Slf4j
@ControllerAdvice(assignableTypes = {MyInfoApiController.class})
@RequiredArgsConstructor
public class ApiExController {

    private final MessageSource ms;

    /**
     * api 이용중 로그인이 안됐을 경우
     *
     * @return
     */
    @ExceptionHandler(UserInfoNotFoundException.class)
    public String userInfoExHandle(UserInfoNotFoundException e, HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        log.info("e = {}", e);
        Alert.waringAlert(ms.getMessage(e.getMessage(), null, request.getLocale()),"/login",response);
        return null;
    }


}

package sky.Sss.domain.user.controller.advice;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import sky.Sss.domain.user.controller.MyInfoController;
import sky.Sss.domain.user.exception.UserInfoNotFoundException;
import sky.Sss.global.utili.Alert;

@Slf4j
@ControllerAdvice(assignableTypes = {MyInfoController.class})
@RequiredArgsConstructor
public class ExController {

    private final MessageSource ms;

    /**
     * 존재하지 않는 사용자인 경우
     * @return
     */
    @ExceptionHandler(UserInfoNotFoundException.class)
    public String userInfoExHandle(HttpServletResponse response, UserInfoNotFoundException e,
        HttpServletRequest request)
        throws IOException {
        Alert.waringAlert(ms.getMessage(e.getMessage(), null, request.getLocale()), "/logout", response);
        return null;
    }


}

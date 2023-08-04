package sky.board.domain.user.utill.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import sky.board.domain.user.entity.UserLoginLog;
import sky.board.domain.user.exception.LoginFailCountException;
import sky.board.domain.user.model.LoginSuccess;
import sky.board.domain.user.service.UserLogService;
import sky.board.domain.user.utill.HttpReqRespUtils;

/**
 * 로그인 실패시 로직을 실행하는 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailHandler implements AuthenticationFailureHandler {


    private final UserLogService userLogService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException {

        StringBuffer sbPath = new StringBuffer(request.getContextPath() + "/login/fail?");

        boolean retryTwoFactor = false;

        if (exception.getClass().equals(LoginFailCountException.class)) {
            log.info("request.getParameter(username) = {}", request.getParameter("userId"));
            retryTwoFactor = true;
        }
        userLogService.saveLoginLog(request, LoginSuccess.FAIL);
        sendRedirect(request, response, sbPath,retryTwoFactor);
    }


    /**
     * 로그인페이지로 이동 메서드
     *
     * @param request
     * @param response
     * @throws IOException
     */
    private void sendRedirect(HttpServletRequest request, HttpServletResponse response, StringBuffer sbPath,
        boolean retryTwoFactor)
        throws IOException {

        String userId = request.getParameter("userId");
        String url = request.getParameter("url");
        String errMsg = "login.error";
        errMsg = URLEncoder.encode(errMsg, "UTF-8");
        sbPath.append("url=" + url);
        sbPath.append("&userId=" + userId);
        sbPath.append("&errMsg=" + errMsg);
        sbPath.append("&retryTwoFactor=" + retryTwoFactor);

        response.sendRedirect(sbPath.toString());
    }

}

package sky.board.domain.user.utill.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import sky.board.domain.user.entity.UserLoginLog;
import sky.board.domain.user.exception.CaptchaMisMatchFactorException;
import sky.board.domain.user.exception.LoginFailCountException;
import sky.board.domain.user.model.LoginSuccess;
import sky.board.domain.user.service.UserLogService;
import sky.board.domain.user.utill.HttpReqRespUtils;
import sky.board.global.openapi.service.ApiExamCaptchaNkeyService;

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
        String errMsg = "login.error";
        /**
         * BadCredentialsException : 비밀번호불일치
         * UsernameNotFoundException : 계정없음
         * AccountExpiredException : 계정만료
         * CredentialsExpiredException : 비밀번호만료
         * DisabledException : 계정비활성화
         * LockedException : 계정잠김
         * MissingCapthcahFactorException : 2차 인증 번호를 입력하지 않음
         * CaptchaMisMatchFactorException : 2차 인증 번호가 맞지 않음
         */

        boolean retryTwoFactor = false;
        // 로그인 실패 횟수가 5번을 넘어가는 경우

        if (exception instanceof LoginFailCountException) {
            retryTwoFactor = true;
            errMsg = "login.error.captcha";
            sbPath.append("imagePath=" + request.getAttribute("imagePath"));
            sbPath.append("&captchaKey=" + request.getAttribute("captchaKey") + "&");

        } else if (exception instanceof CaptchaMisMatchFactorException) { // 2차 인증번호가 맞지 않은 경우
            errMsg = "login.error.captcha";
        } else if (exception instanceof UsernameNotFoundException) { //

        }

        userLogService.saveLoginLog(request, LoginSuccess.FAIL);
        sendRedirect(request, response, errMsg, sbPath, retryTwoFactor);
    }


    /**
     * 로그인페이지로 이동 메서드
     *
     * @param request
     * @param response
     * @throws IOException
     */
    private void sendRedirect(HttpServletRequest request, HttpServletResponse response, String errMsg,
        StringBuffer sbPath,
        boolean retryTwoFactor)
        throws IOException {

        String userId = request.getParameter("userId");
        String url = request.getParameter("url");

        // 2차 인증 번호 생성될 경우 메시지가 바뀜
        if (retryTwoFactor) {
            errMsg = "login.error.captcha";
        }

        errMsg = URLEncoder.encode(errMsg, "UTF-8");
        sbPath.append("url=" + url);
        sbPath.append("&userId=" + userId);
        sbPath.append("&errMsg=" + errMsg);
        sbPath.append("&retryTwoFactor=" + retryTwoFactor);
        sbPath.append("&error=" + true);

        response.sendRedirect(sbPath.toString());
    }

}

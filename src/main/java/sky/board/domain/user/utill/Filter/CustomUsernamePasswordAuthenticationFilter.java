package sky.board.domain.user.utill.Filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import sky.board.domain.user.exception.CaptchaMisMatchFactorException;
import sky.board.domain.user.exception.LoginFailCountException;
import sky.board.domain.user.exception.MissingCapthcahFactorException;
import sky.board.domain.user.model.LoginSuccess;
import sky.board.domain.user.model.PathDetails;
import sky.board.domain.user.service.UserLogService;
import sky.board.global.openapi.service.ApiExamCaptchaNkeyService;

@Slf4j
@Component(value = "authenticationFilter")
@RequiredArgsConstructor
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final UserLogService userLogService;
    private boolean postOnly = true;
    private final ApiExamCaptchaNkeyService apiExamCaptchaNkeyService;

    @Autowired
    public CustomUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager,
        UserLogService userLogService, AuthenticationFailureHandler authenticationFailureHandler,
        AuthenticationSuccessHandler authenticationSuccessHandler,
        ApiExamCaptchaNkeyService apiExamCaptchaNkeyService, ObjectMapper objectMapper) {
        super(authenticationManager);
        super.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        super.setAuthenticationFailureHandler(authenticationFailureHandler);
        this.userLogService = userLogService;
        this.apiExamCaptchaNkeyService = apiExamCaptchaNkeyService;
    }


    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        final int LIMIT = 4;

        /**
         * 유저가 로그인 버튼을 입력한 URL 저장
         */

        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        String chptchaKey = getRequestValue("captchaKey", request);

        // 2차인증 키가 있을 경우 인증번호를 확인하고 널일 경우 반환
//        result = {"result":false,"errorMessage":"Invalid key.","errorCode":"CT001"}

        // 2차 인증 성공시 true,아니며 false 발급을 안 받았을경우도 false
        boolean isCaptcha = false;
        if (StringUtils.hasText(chptchaKey)) {
            String captcha = request.getParameter("captcha");
            captcha = (captcha != null) ? captcha.trim() : "";

            String filename = getRequestValue("filename", request);
            if (!StringUtils.hasText(captcha)) {
                throw new UsernameNotFoundException("captcha Not found");
            }
            // 2차 인증 키 검증
            Map result = apiExamCaptchaNkeyService.getApiExamCaptchaNkeyResult(filename, chptchaKey,
                captcha);
            isCaptcha = (boolean) result.get("result");

            // 번호가 맞지 않은 경우
            if (!isCaptcha) {
                throw new CaptchaMisMatchFactorException("CaptchaMisMatchFactorException");
            }

        }

        String userId = getRequestValue("userId", request);
        String password = getRequestValue("password", request);

        // 아이디를 아무것도 입력하지 않았을 경우
        if ((!StringUtils.hasText(userId))) {
            throw new UsernameNotFoundException("userId Not found");
        }
        // 비밀번호를 아무것도 입력하지 않았을 경우
        if ((!StringUtils.hasText(password))) {
            throw new UsernameNotFoundException("password Not found");
        }

        Long failCount = userLogService.getLoginLogCount(userId, LoginSuccess.FAIL);

        // 로그인 실패가 5번 이상 일 경우 & 2차인증을 성공하지 못했을 경우
        if (LIMIT <= failCount && !isCaptcha) {
            Map mapKey = apiExamCaptchaNkeyService.getApiExamCaptchaNkey();
            String key = (String) mapKey.get("key");
            String image = apiExamCaptchaNkeyService.getApiExamCaptchaImage(key);
            request.setAttribute("captchaKey", key);
            request.setAttribute("imagePath", PathDetails.getFilePath(PathDetails.CAPTCHA_IMAGE_URL, image, "jpg"));

            throw new LoginFailCountException("Login count < 5");
        }

        UsernamePasswordAuthenticationToken authRequest =
            UsernamePasswordAuthenticationToken.unauthenticated(userId, password);
        super.setDetails(request, authRequest);

        Authentication authentication = this.getAuthenticationManager().authenticate(authRequest);

        return authentication;
    }

    private String getRequestValue(String key, HttpServletRequest request) {
        String value = request.getParameter(key);
        value = (value != null) ? value.trim() : "";
        return value;
    }

    @Override
    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
        super.setPostOnly(postOnly);
    }
}

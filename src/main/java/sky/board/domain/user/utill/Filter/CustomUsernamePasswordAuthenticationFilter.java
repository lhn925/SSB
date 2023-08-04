package sky.board.domain.user.utill.Filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import sky.board.domain.user.exception.LoginFailCountException;
import sky.board.domain.user.model.LoginSuccess;
import sky.board.domain.user.service.UserLogService;

@Slf4j
@Component(value = "authenticationFilter")
@RequiredArgsConstructor
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final UserLogService userLogService;
    private boolean postOnly = true;
    @Autowired
    public CustomUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager,
        UserLogService userLogService,AuthenticationFailureHandler authenticationFailureHandler,AuthenticationSuccessHandler authenticationSuccessHandler) {
        super(authenticationManager);
        super.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        super.setAuthenticationFailureHandler(authenticationFailureHandler);
        this.userLogService = userLogService;
    }


    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        final int LIMIT = 5;

        /**
         * 유저가 로그인 버튼을 입력한 URL 저장
         */

        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        String userId = request.getParameter("userId");
        userId = (userId != null) ? userId.trim() : "";

        String password = request.getParameter("password");
        password = (password != null) ? password : "";

        Long failCount = userLogService.getLoginLogCount(userId, LoginSuccess.FAIL);

        // 로그인 실패가 5번 이상 일 경우
        if (LIMIT <= failCount) {
            throw new LoginFailCountException("Login count < 5");
        }
        UsernamePasswordAuthenticationToken authRequest =
            UsernamePasswordAuthenticationToken.unauthenticated(userId, password);
        super.setDetails(request, authRequest);

        Authentication authentication = this.getAuthenticationManager().authenticate(authRequest);

        return authentication;
    }

    @Override
    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
        super.setPostOnly(postOnly);
    }
}

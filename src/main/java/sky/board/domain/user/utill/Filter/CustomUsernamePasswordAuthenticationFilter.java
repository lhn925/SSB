package sky.board.domain.user.utill.Filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sky.board.domain.user.dto.CustomUserDetails;

@Slf4j
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private boolean postOnly = true;

    public CustomUsernamePasswordAuthenticationFilter(
        AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        log.info("안녕하세요 여기는");
        /**
         * 유저가 로그인 버튼을 입력한 URL 저장
         */

        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        String username = request.getParameter("userId");
        username = (username != null) ? username.trim() : "";

        String password = request.getParameter("password");
        password = (password != null) ? password : "";

        String url = request.getParameter("url");
        url = (url != null) ? url : "/";

        UsernamePasswordAuthenticationToken authRequest =
            UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        super.setDetails(request, authRequest);

        Authentication authentication = this.getAuthenticationManager().authenticate(authRequest);

        return authentication;
    }


    @Override
    public void setAuthenticationFailureHandler(AuthenticationFailureHandler failureHandler) {
        log.info("fail handler {}", failureHandler.getClass());
        super.setAuthenticationFailureHandler(failureHandler);
    }

    @Override
    public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler successHandler) {
        log.info("successHandler handler {}", successHandler.getClass());
        super.setAuthenticationSuccessHandler(successHandler);
    }
}

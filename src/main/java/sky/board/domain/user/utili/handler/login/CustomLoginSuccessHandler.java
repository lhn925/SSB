package sky.board.domain.user.utili.handler.login;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.security.auth.login.LoginException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public interface CustomLoginSuccessHandler extends AuthenticationSuccessHandler {

    @Override
    default void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
        Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException;

    void sendRedirect(HttpServletResponse response, String url, String redirectUrl) throws IOException;

    void saveContext(HttpServletRequest request, HttpServletResponse response, Authentication authentication);

    void setSession(HttpServletRequest request, Authentication authentication);

    /**
     * 로그인 기기 저장
     * @param request
     * @param response
     * @param authentication
     */
    void saveLoginStatus(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
        throws LoginException;
}

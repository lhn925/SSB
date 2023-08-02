package sky.board.global.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HandlesTypes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import sky.board.domain.user.utill.Filter.CustomUsernamePasswordAuthenticationFilter;

/**
 * 로그인 실패시 로직을 실행하는 핸들러
 */
@Slf4j
@Component
public class CustomAuthenticationFailHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException, ServletException {
        log.info("CustomAuthenticationFailHandler {}", exception.getMessage());
        response.sendRedirect(request.getContextPath() + "/login");
    }
}

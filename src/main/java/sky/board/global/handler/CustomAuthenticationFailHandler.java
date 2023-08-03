package sky.board.global.handler;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.SimpleAssociationHandler;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import sky.board.domain.user.dto.CustomUserDetails;

/**
 * 로그인 실패시 로직을 실행하는 핸들러
 */
@Slf4j
public class CustomAuthenticationFailHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException {

        String userId = request.getParameter("userId");
        String url = request.getParameter("url");
        String password = request.getParameter("password");

        log.info("userId = {}", userId);
        log.info("url = {}", url);
        log.info("password = {}", password);
        String errMsg = "login.error";
        errMsg = URLEncoder.encode(errMsg, "UTF-8");
        response.sendRedirect(request.getContextPath() + "/login?url=" + url + "&errMsg=" + errMsg + "&error=" + true);
    }

}

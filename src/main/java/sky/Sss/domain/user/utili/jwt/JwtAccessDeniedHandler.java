package sky.Sss.domain.user.utili.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

// 요청한 권한이 없는 경우
@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        //권한이 없을 경우 403
        log.info("JwtAccessDeniedHandler = {}");
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
}
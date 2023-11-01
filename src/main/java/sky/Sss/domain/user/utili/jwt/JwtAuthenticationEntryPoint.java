package sky.Sss.domain.user.utili.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import sky.Sss.domain.user.exception.RefreshTokenNotFoundException;


//유효한 jwt토큰 없이 접근하려 할때 401 Unauthorized 에러를 리턴하는 class
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {
        // 유효하지 않을 때 401
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONObject exObject = new JSONObject();
        log.info("authException.getClass() = {}", authException.getClass());
        // accessToken이 유효하지 않을떄
        if (authException instanceof RefreshTokenNotFoundException) {
            exObject.put("code", "ex");
        } else { // 토큰이 아예 없을 떄
            exObject.put("code", "not");
        }
        response.setStatus(401);
        response.getWriter().write(exObject.toJSONString());
    }
}

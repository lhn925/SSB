package sky.Sss.domain.user.utili.handler.logout;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import sky.Sss.domain.user.dto.UserInfoDto;
import sky.Sss.domain.user.model.RememberCookie;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.login.RedisRememberService;
import sky.Sss.domain.user.service.login.UserLoginStatusService;
import sky.Sss.domain.user.utili.CustomCookie;
import sky.Sss.domain.user.utili.jwt.JwtFilter;
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisService;


@Slf4j
//@Component
@RequiredArgsConstructor
public class CustomSimpleUrlLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    private final UserLoginStatusService userLoginStatusService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
        throws IOException, ServletException {

        String header = request.getHeader(JwtFilter.AUTHORIZATION_HEADER);

        log.info("header = {}", header);

        log.info("onLogoutSuccess ");
        // 로그인 상태 변경
        log.info("authentication = {}", authentication.getPrincipal());
        // 세션 삭제
        response.sendRedirect(request.getContextPath()+"/logout");
        super.onLogoutSuccess(request, response, authentication);
    }

    private void setUrl(HttpServletRequest request) {
        String url = request.getParameter("url");
        if (url == null || !StringUtils.hasText(url)) {
            url = "/";
        }
        setDefaultTargetUrl(url);
    }
}

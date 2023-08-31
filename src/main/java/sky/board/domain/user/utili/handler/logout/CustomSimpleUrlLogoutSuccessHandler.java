package sky.board.domain.user.utili.handler.logout;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.CacheProperties.Redis;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import sky.board.domain.user.model.RememberCookie;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.service.login.RedisRememberService;
import sky.board.domain.user.service.login.UserLoginStatusService;
import sky.board.domain.user.utili.CustomCookie;
import sky.board.global.redis.service.RedisService;


@Slf4j
@Component
@RequiredArgsConstructor
public class CustomSimpleUrlLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {


    private final RememberMeServices rememberMeServices;
    private final UserLoginStatusService userLoginStatusService;
    private final RedisService redisService;
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
        throws IOException, ServletException {
        Cookie[] cookies = request.getCookies();
        String hashKey = CustomCookie.readCookie(cookies, RememberCookie.KEY.getValue());

        // Redis에 저장되어 있는 rememberMe 데이터 삭제
        if (hashKey != null && StringUtils.hasText(hashKey)) {
            RedisRememberService redisRememberService = (RedisRememberService) rememberMeServices;
            String redisKey = redisRememberService.hashing(hashKey);
            redisService.deleteRemember(redisKey);
        }
        setUrl(request);
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

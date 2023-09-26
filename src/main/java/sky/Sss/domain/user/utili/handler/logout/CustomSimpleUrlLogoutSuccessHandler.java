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
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisService;


@Slf4j
@Component
@RequiredArgsConstructor
public class CustomSimpleUrlLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    private final UserLoginStatusService userLoginStatusService;
    private final RememberMeServices rememberMeServices;
    private final RedisService redisService;
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
        throws IOException, ServletException {
        Cookie[] cookies = request.getCookies();
        String hashKey = CustomCookie.readCookie(cookies, RememberCookie.KEY.getValue());

        log.info("onLogoutSuccess ");
        HttpSession session = request.getSession(false);
        // 로그인 상태 변경
        if (session != null &&
            session.getAttribute(RedisKeyDto.USER_KEY) != null) {
            UserInfoDto userInfoDto = (UserInfoDto) session.getAttribute(RedisKeyDto.USER_KEY);
            userLoginStatusService.updateLoginStatus(request, userInfoDto.getUserId(),
                Status.OFF,
                Status.OFF);
            session.invalidate();
        }

        // Redis에 저장되어 있는 rememberMe 데이터 삭제
        if (hashKey != null && StringUtils.hasText(hashKey)) {
            RedisRememberService redisRememberService = (RedisRememberService) rememberMeServices;
            String redisKey = redisRememberService.hashing(hashKey);

            redisService.deleteRemember(redisKey);
        }
        setUrl(request);
        // 세션 삭제
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

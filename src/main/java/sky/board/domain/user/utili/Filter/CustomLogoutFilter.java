package sky.board.domain.user.utili.Filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import sky.board.domain.user.dto.UserInfoDto;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.service.login.UserLoginStatusService;
import sky.board.global.redis.dto.RedisKeyDto;

@Slf4j
public class CustomLogoutFilter extends LogoutFilter {


    private final UserLoginStatusService userLoginStatusService;

    public CustomLogoutFilter(
        LogoutSuccessHandler logoutSuccessHandler,
        UserLoginStatusService userLoginStatusService, LogoutHandler... handlers) {
        super(logoutSuccessHandler, new SecurityContextLogoutHandler());
        this.userLoginStatusService = userLoginStatusService;
    }

    public CustomLogoutFilter(String logoutSuccessUrl, UserLoginStatusService userLoginStatusService,
        LogoutHandler... handlers) {
        super(logoutSuccessUrl, new SecurityContextLogoutHandler());
        this.userLoginStatusService = userLoginStatusService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        if (super.requiresLogout((HttpServletRequest) request, (HttpServletResponse) response)) {
            log.info("CustomLogoutFilter = {}");
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            HttpSession session = httpServletRequest.getSession(false);
            // 로그인 상태 변경
            if (session != null &&
                session.getAttribute(RedisKeyDto.USER_KEY) != null) {
                UserInfoDto userInfoDto = (UserInfoDto) session.getAttribute(RedisKeyDto.USER_KEY);
                userLoginStatusService.updateLoginStatus((HttpServletRequest) request, userInfoDto.getUserId(), Status.OFF,
                    Status.OFF);
            }
        }
        super.doFilter(request, response, chain);
    }


}

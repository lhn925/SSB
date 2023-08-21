package sky.board.domain.user.utill.handler;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import sky.board.domain.user.dto.login.CustomUserDetails;
import sky.board.domain.user.dto.UserInfoSessionDto;
import sky.board.global.redis.dto.RedisKeyDto;

@Slf4j
@Component
public class CustomCookieLoginSuccessHandler implements CustomLoginSuccessHandler {


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
        Authentication authentication) throws IOException, ServletException {
        CustomLoginSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {
        request.getRequestURL();
        setSession(request, authentication);
        log.info("request.getRequestURI() = {}", request.getRequestURI());
        sendRedirect(response, request.getRequestURL().toString(), request.getContextPath());
    }


    @Override
    public void sendRedirect(HttpServletResponse response, String url, String redirectUrl) throws IOException {
        response.sendRedirect(redirectUrl + url);
    }

    @Override
    public void saveContext(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

    }

    @Override
    public void setSession(HttpServletRequest request, Authentication authentication) {
        HttpSession session = request.getSession();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserInfoSessionDto userInfo = UserInfoSessionDto.createUserInfo(userDetails);
        session.setAttribute(RedisKeyDto.USER_KEY, userInfo);
    }


}

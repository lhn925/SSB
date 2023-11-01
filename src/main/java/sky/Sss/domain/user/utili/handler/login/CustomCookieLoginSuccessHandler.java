package sky.Sss.domain.user.utili.handler.login;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import sky.Sss.domain.user.dto.UserInfoDto;
import sky.Sss.domain.user.dto.login.CustomUserDetails;
import sky.Sss.domain.user.service.login.UserLoginStatusService;
import sky.Sss.domain.user.utili.UserTokenUtil;
import sky.Sss.domain.user.utili.jwt.JwtTokenDto;
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisService;

@Slf4j
//@Component
@RequiredArgsConstructor
public class CustomCookieLoginSuccessHandler extends CustomLoginSuccessHandler {


    private final UserLoginStatusService userLoginStatusService;
    private final RedisService redisService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
        Authentication authentication) throws IOException, ServletException {
        super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {
        request.getRequestURL();
        setSession(request, authentication);
        saveLoginStatus(request,response,authentication);
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
        UserInfoDto userInfo = UserInfoDto.createUserInfo(userDetails);

        setLoginToken(redisService, request, userInfo);
        session.setAttribute(RedisKeyDto.USER_KEY, userInfo);

    }

    @Override
    public void saveLoginStatus(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {
        try {
//            userLoginStatusService.save(request.getHeader("User-Agent"),new JwtTokenDto(),null);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("saveLoginStatus:" + e);
        }
    }

}

package sky.Sss.domain.user.utili.handler.login;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.security.auth.login.LoginException;
import org.json.simple.JSONObject;
import org.springframework.boot.autoconfigure.cache.CacheProperties.Redis;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import sky.Sss.domain.user.dto.UserInfoDto;
import sky.Sss.domain.user.utili.UserTokenUtil;
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisService;

public abstract class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
        Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public abstract void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException;

    public abstract void saveContext(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication);
    public abstract void sendRedirect(HttpServletResponse response, String url, String redirectUrl) throws IOException;

    public abstract void setSession(HttpServletRequest request, Authentication authentication);

    public void setLoginToken(RedisService redisService,HttpServletRequest request, UserInfoDto userInfo) {
        String redisToken = UserTokenUtil.getToken();
        request.setAttribute(RedisKeyDto.REDIS_LOGIN_KEY, redisToken);
        JSONObject userObject = new JSONObject();
        userObject.put("userId", userInfo.getUserId());
        userObject.put("email", userInfo.getEmail());
        userObject.put("pictureUrl", userInfo.getPictureUrl());
        redisService.setData(RedisKeyDto.REDIS_LOGIN_KEY + redisToken, userObject.toJSONString(), 1800000L);
    }

    /**
     * 로그인 기기 저장
     *
     * @param request
     * @param response
     * @param authentication
     */
    public abstract void saveLoginStatus(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
        throws LoginException;
}

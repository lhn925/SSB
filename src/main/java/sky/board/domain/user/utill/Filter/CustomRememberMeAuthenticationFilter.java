package sky.board.domain.user.utill.Filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import sky.board.domain.user.dto.CustomUserDetails;
import sky.board.domain.user.model.RememberCookie;
import sky.board.domain.user.service.RedisRememberService;
import sky.board.domain.user.utill.ReadCookie;
import sky.board.domain.user.utill.UserTokenUtil;
import sky.board.global.redis.dto.RedisKeyDto;

@Slf4j
public class CustomRememberMeAuthenticationFilter extends RememberMeAuthenticationFilter {

    public CustomRememberMeAuthenticationFilter(
        AuthenticationManager authenticationManager,
        RememberMeServices rememberMeServices,
        AuthenticationSuccessHandler successHandler
    ) {
        super(authenticationManager, rememberMeServices);
        log.info("authenticationManager.getClass() = {}", authenticationManager.getClass());
        super.setAuthenticationSuccessHandler(successHandler);
        ProviderManager providerManager = (ProviderManager) authenticationManager;
        providerManager.getProviders().add(new RememberMeAuthenticationProvider("rememberMe"));
    }


    @Override
    protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
        Authentication authResult) {
        RedisRememberService rememberMeServices = (RedisRememberService) super.getRememberMeServices();

        // 쿠키에 있는 값을 가져온 뒤
        String key = ReadCookie.readCookie(request.getCookies(), RememberCookie.KEY.getValue());

        log.info("rememberMe value = {}", key);
        String redisToken = rememberMeServices.hashing(key);

        log.info("redisToken = {}", redisToken);

        // 새롭게 쿠키에 저장될 토큰 생성
        String token = UserTokenUtil.getToken();

        CustomUserDetails principal = (CustomUserDetails) authResult.getPrincipal();

        int tokenLifetime = RedisRememberService.TWO_WEEKS_S; // 2주
        long expiryTime = rememberMeServices.getExpiryTime(tokenLifetime);

        rememberMeServices.publicSetCookie(null, tokenLifetime, request, response, token);
        rememberMeServices.setRedis(principal.getUserId(), principal.getPassword(), request.getSession(), expiryTime,
            token);

        String deleteKey = key.split(":")[0];

        log.info("deleteKey = {}", deleteKey);
        log.info("RedisKeyDto.SESSION_KEY = {}", RedisKeyDto.SESSION_KEY);
        rememberMeServices.getRedisService().deleteData(redisToken);
        rememberMeServices.getRedisService().deleteData(RedisKeyDto.SESSION_KEY + deleteKey);
    }

    @Override
    protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException failed) {
        super.onUnsuccessfulAuthentication(request, response, failed);


    }
}

package sky.board.domain.user.utili.Filter;

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
import sky.board.domain.user.dto.login.CustomUserDetails;
import sky.board.domain.user.model.RememberCookie;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.service.login.RedisRememberService;
import sky.board.domain.user.utili.CustomCookie;
import sky.board.domain.user.utili.UserTokenUtil;

@Slf4j
public class CustomRememberMeAuthenticationFilter extends RememberMeAuthenticationFilter {

    public CustomRememberMeAuthenticationFilter(
        AuthenticationManager authenticationManager,
        RememberMeServices rememberMeServices,
        AuthenticationSuccessHandler successHandler
    ) {
        super(authenticationManager, rememberMeServices);
        super.setAuthenticationSuccessHandler(successHandler);
        ProviderManager providerManager = (ProviderManager) authenticationManager;
        providerManager.getProviders().add(new RememberMeAuthenticationProvider("rememberMe"));
    }


    /**
     * 로그인에 성공 했을경우
     *
     * @param request
     * @param response
     * @param authResult
     */
    @Override
    protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
        Authentication authResult) {
        RedisRememberService rememberMeServices = (RedisRememberService) super.getRememberMeServices();

        // 쿠키에 있는 값을 가져온 뒤
        String key = CustomCookie.readCookie(request.getCookies(), RememberCookie.KEY.getValue());

        String rmRedisToken = rememberMeServices.hashing(key);

        // 새롭게 쿠키에 저장될 토큰 생성
        String token = UserTokenUtil.getToken();

        CustomUserDetails principal = (CustomUserDetails) authResult.getPrincipal();

        int tokenLifetime = RedisRememberService.TWO_WEEKS_S; // 2주
        long expiryTime = rememberMeServices.getExpiryTime(tokenLifetime);

        rememberMeServices.publicSetCookie(tokenLifetime, request, response, token);

        rememberMeServices.setRedis(principal.getUserId(), request.getSession(), expiryTime,
            token);

        String rmSessionKey = key.split(":")[0];

        // 레디스 데이터 삭제
        rememberMeServices.getRedisService().deleteRemember(rmRedisToken);
        rememberMeServices.getRedisService().deleteSession(rmSessionKey);

        // DB LoginStatus 컬럼값 변경
        rememberMeServices.getUserLoginStatusService()
            .logoutRememberLoginStatus(((CustomUserDetails) authResult.getPrincipal()).getUserId(), rmSessionKey,
                rmRedisToken,
                Status.ON, Status.ON);
    }

    @Override
    protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException failed) {
        super.onUnsuccessfulAuthentication(request, response, failed);


    }
}

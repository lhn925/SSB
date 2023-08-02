package sky.board.global.handler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import sky.board.domain.user.utill.CustomUsernamePasswordAuthenticationToken;

/**
 * 로그인 성공 시 로직을 실행하는 핸들러
 */
@Slf4j
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
        Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {

        CustomUsernamePasswordAuthenticationToken authentication1 = (CustomUsernamePasswordAuthenticationToken) authentication;

        log.info("getUrl {}", authentication1.getUrl());
    }
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication,CustomUsernamePasswordAuthenticationToken token) throws IOException, ServletException {
        log.info("token {}", token.getUrl().toString());
        authentication.getAuthorities().stream().forEach(e -> log.info("getAuthority {}",e.getAuthority()));
        log.info("getDetails {}",authentication.getDetails());
        log.info("getCredentials {}",authentication.getCredentials());
        log.info("getPrincipal {}",authentication.getPrincipal());
        log.info("getName {}",authentication.getName());
        log.info("getClass {}",authentication.getClass());

        /**
         * getAuthority ROLE_USER
         * getDetails WebAuthenticationDetails [RemoteIpAddress=127.0.0.1, SessionId=3A30B85F5F35480EC49720C104E3B5C3]
         * getCredentials null
         * getPrincipal org.springframework.security.core.userdetails.User [Username=0dksmf071, Password=[PROTECTED], Enabled=true, AccountNonExpired=true, credentialsNonExpired=true, AccountNonLocked=true, Granted Authorities=[ROLE_USER]]
         * getName 0dksmf071
         * getClass class org.springframework.security.authentication.UsernamePasswordAuthenticationToken
         */





    }
}

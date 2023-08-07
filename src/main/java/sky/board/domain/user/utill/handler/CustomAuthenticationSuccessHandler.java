package sky.board.domain.user.utill.handler;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.xdevapi.JsonArray;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

/**
 * 로그인 성공 시 로직을 실행하는 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {


    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
        Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {

        HttpSession session = request.getSession();

        JsonArray loginList = (JsonArray) session.getAttribute("login");

        if (loginList == null || loginList.size() == 0) {
            session.setAttribute("login", new JsonArray());
        }



        WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();

        log.info("details.toString() = {}", details.toString());


        log.info("url {}", request.getParameter("url"));
        log.info("getDetails {}", authentication.getDetails());
        log.info("getCredentials {}", authentication.getCredentials());
        log.info("getPrincipal {}", authentication.getPrincipal());
        log.info("getName {}", authentication.getName());
        log.info("getClass {}", authentication.getClass());

//        url
//        getDetails WebAuthenticationDetails [RemoteIpAddress=127.0.0.1, SessionId=null]
//        getCredentials null
//        getPrincipal sky.board.domain.user.dto.CustomUserDetails [Username=0유입니다2, Password=[PROTECTED], Enabled=true, url=null, AccountNonExpired=true, credentialsNonExpired=true, AccountNonLocked=true, Granted Authorities=[ROLE_USER]]
//        getName 0유입니다2
//        getClass class org.springframework.security.authentication.UsernamePasswordAuthenticationToken

    }


}

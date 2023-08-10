package sky.board.domain.user.utill.handler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import sky.board.domain.user.dto.CustomUserDetails;
import sky.board.domain.user.dto.UserInfoSessionDto;
import sky.board.domain.user.model.LoginSuccess;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.service.UserDetailsCustomService;
import sky.board.domain.user.service.UserLogService;

/**
 * 로그인 성공 시 로직을 실행하는 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {


    private final UserLogService userLogService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
        Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {
        HttpSession session = request.getSession();
        /**
         * creationTime 세션 생성시간
         * lastAccessedTime 마지막 세션 조회 시간
         * sessionAttr 세션에 저장한 데이터
         * maxInactiveInterval 만료시간
         */

        UserInfoSessionDto result = (UserInfoSessionDto) session.getAttribute("USER_ID");

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        UserInfoSessionDto userInfo = UserInfoSessionDto.createUserInfo(userDetails);
        if (result == null) {
            session.setAttribute("USER_ID", userInfo);
        }

        //로그인 성공 기록 저장
        userLogService.saveLoginLog(request, LoginSuccess.SUCCESS, Status.ON);

        // 로그인 실패 기록 다 삭제
        userLogService.deleteLoginLog(request, LoginSuccess.FAIL, Status.OFF);

        String url = request.getParameter("url");

        log.info("url = {}", url);

        String redirectUrl = request.getContextPath() + "/";
        if (StringUtils.hasText(url)) {
            redirectUrl = url;
            log.info("redirectUrl = {}", redirectUrl);
        }
        response.sendRedirect(redirectUrl);
    }


}

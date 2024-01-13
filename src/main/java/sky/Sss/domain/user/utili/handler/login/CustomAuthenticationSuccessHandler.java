package sky.Sss.domain.user.utili.handler.login;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import sky.Sss.domain.user.dto.login.CustomUserDetails;
import sky.Sss.domain.user.dto.UserInfoDto;
import sky.Sss.domain.user.model.LoginSuccess;
import sky.Sss.domain.user.model.RememberCookie;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.log.UserLoginLogService;
import sky.Sss.domain.user.service.login.UserLoginStatusService;
import sky.Sss.domain.user.utili.jwt.JwtFilter;
import sky.Sss.domain.user.utili.jwt.JwtTokenDto;
import sky.Sss.domain.user.utili.jwt.TokenProvider;
import sky.Sss.global.openapi.service.ApiExamCaptchaNkeyService;
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisService;

/**
 * 로그인 성공 시 로직을 실행하는 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends
    CustomLoginSuccessHandler {

    private final UserLoginLogService userLoginLogService;
    private final HttpSessionSecurityContextRepository securityContextRepository;
    private final SecurityContextImpl securityContext;
    private final ApiExamCaptchaNkeyService apiExamCaptchaNkeyService;
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
        setSession(request, authentication);

        // 로그인 유지를 체크하였다면 쿠키 및 세션에 저장? nono
        // 2주 동안 유지 활동이 없으면 삭제 혹은 로그아웃을 하면 삭제
        /**
         * 단, 2주 동안 해당 PC에서 사이트를 사용하지 않는다면 로그인 상태 유지는 해제될 수 있습니다.
         * - 개인 정보 보호를 위해 공용 PC에서는 사용에 유의해 주시기 바랍니다.
         */
        saveContext(request, response, authentication);

        String imageName = request.getParameter("imageName");
        if (StringUtils.hasText(imageName)) { // 성공시 삭제
            apiExamCaptchaNkeyService.deleteImage(imageName);
        }
        // 로그인 실패 기록 다 삭제
        Optional.ofNullable(request.getAttribute("failCount")).ifPresent(
            c -> userLoginLogService.delete(request, LoginSuccess.FAIL, Status.OFF)
        );

        // 해당 객체를 SecurityContextHolder에 저장하고
        // authentication 객체를 createToken 메소드를 통해서 JWT Token을 생성
//        String jwt = tokenProvider.createToken(authentication);

        // response header에 jwt token에 넣어줌
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
//        log.info("jwt = {}", jwt);
        saveLoginStatus(request, response, authentication);
        String url = request.getParameter("url");
        String redirectUrl = request.getContextPath() + "/";
        sendRedirect(response, url, redirectUrl);
    }

    @Override
    public void sendRedirect(HttpServletResponse response, String url, String redirectUrl) throws IOException {
        if (StringUtils.hasText(url)) {
            redirectUrl = url;
        }
        response.sendRedirect(redirectUrl);
    }

    // 유저 인증 정보 저장
    @Override
    public void saveContext(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        securityContext.setAuthentication(authentication);
        securityContextRepository.saveContext(securityContext, request, response);
        //로그인 성공 기록 저장
        userLoginLogService.save(request.getHeader("User-Agent"), request.getParameter("userId"), LoginSuccess.SUCCESS,
            Status.ON);
    }

    /**
     * creationTime 세션 생성시간
     * lastAccessedTime 마지막 세션 조회 시간
     * sessionAttr 세션에 저장한 데이터
     * maxInactiveInterval 만료시간
     */
    @Override
    public void setSession(HttpServletRequest request, Authentication authentication) {
        HttpSession session = request.getSession();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserInfoDto userInfo = UserInfoDto.createUserInfo(userDetails);

        // 리액트단에서 접근할 유저정보 저장
        setLoginToken(redisService, request, userInfo);
        session.setAttribute(RedisKeyDto.USER_KEY, userInfo);
    }


    @Override
    public void saveLoginStatus(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {
        try {
            String remember = (String) request.getSession().getAttribute(RememberCookie.KEY.getValue());

            if (remember != null) {
                request.setAttribute(RememberCookie.KEY.getValue(), remember);
                request.getSession().removeAttribute(RememberCookie.KEY.getValue());
            }

//            userLoginStatusService.savePlayLog(request.getHeader("User-Agent"),new JwtTokenDto(),null);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("saveLoginStatus: " + e.getMessage());
        }
    }
}

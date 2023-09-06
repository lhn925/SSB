package sky.board.domain.user.utili.handler.login;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import sky.board.domain.user.dto.login.CustomUserDetails;
import sky.board.domain.user.dto.UserInfoDto;
import sky.board.domain.user.model.LoginSuccess;
import sky.board.domain.user.model.RememberCookie;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.service.log.UserLoginLogService;
import sky.board.domain.user.service.login.UserLoginStatusService;
import sky.board.global.openapi.service.ApiExamCaptchaNkeyService;
import sky.board.global.redis.dto.RedisKeyDto;

/**
 * 로그인 성공 시 로직을 실행하는 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements
    CustomLoginSuccessHandler {

    private final UserLoginLogService userLoginLogService;
    private final HttpSessionSecurityContextRepository securityContextRepository;
    private final SecurityContextImpl securityContext;
    private final ApiExamCaptchaNkeyService apiExamCaptchaNkeyService;
    private final UserLoginStatusService userLoginStatusService;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
        Authentication authentication) throws IOException, ServletException {
        CustomLoginSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
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
        userLoginLogService.save(request, LoginSuccess.SUCCESS, Status.ON);
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
            userLoginStatusService.save(request);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("saveLoginStatus: "+e.getMessage());
        }
    }
}

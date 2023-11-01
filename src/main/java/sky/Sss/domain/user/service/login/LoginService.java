package sky.Sss.domain.user.service.login;


import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import sky.Sss.domain.user.dto.login.UserLoginFormDto;
import sky.Sss.domain.user.exception.CaptchaMisMatchFactorException;
import sky.Sss.domain.user.exception.LoginFailException;
import sky.Sss.domain.user.exception.UserInfoNotFoundException;
import sky.Sss.domain.user.model.LoginSuccess;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.log.UserLoginLogService;
import sky.Sss.domain.user.utili.jwt.JwtDto;
import sky.Sss.domain.user.utili.jwt.JwtFilter;
import sky.Sss.domain.user.utili.jwt.JwtTokenDto;
import sky.Sss.domain.user.utili.jwt.TokenProvider;
import sky.Sss.global.error.dto.Result;
import sky.Sss.global.openapi.service.ApiExamCaptchaNkeyService;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class LoginService {

    private final UserLoginLogService userLoginLogService;
    private final ApiExamCaptchaNkeyService apiExamCaptchaNkeyService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final UserLoginStatusService userLoginStatusService;
    private final MessageSource ms;


    @Transactional
    public JwtDto login(UserLoginFormDto userLoginFormDto, String userAgent, String sessionId,Long failCount) {
        /**
         * 유저가 로그인 버튼을 입력한 URL 저장
         */
        String userId = userLoginFormDto.getUserId();
        String password = userLoginFormDto.getPassword();

        // 아이디 및 비번 확인 jwt 발급
        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(
            userId,
            password);
        Authentication authenticate = authenticationManagerBuilder.getObject().authenticate(authRequest);

        // 로그인이 성공 했을 경우
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        JwtTokenDto jwtTokenDto = tokenProvider.createToken(authenticate);

        UserDetails userDetails = (UserDetails) authenticate.getPrincipal();
        HttpHeaders headers = new HttpHeaders();
        if (authenticate.isAuthenticated()) {
            headers.add(JwtFilter.AUTHORIZATION_HEADER, jwtTokenDto.getAccessToken());
            headers.add(JwtFilter.REFRESH_AUTHORIZATION_HEADER, jwtTokenDto.getRefreshToken());
            loginSuccess(failCount, userLoginFormDto);
            saveLoginStatus(userAgent, jwtTokenDto, userDetails, sessionId);
        }
        // redisToken 미포함
        JwtDto jwtDto = new JwtDto(jwtTokenDto.getAccessToken(), jwtTokenDto.getRefreshToken());
        return jwtDto;
    }

    public Boolean verifyCaptchKey(UserLoginFormDto userLoginFormDto, Long failCount, String chptchaKey,Long limit) throws CaptchaMisMatchFactorException {
        Boolean isCaptcha = false; // 인증코드 성공 여부
        String captcha = userLoginFormDto.getCaptcha();
        Boolean isFailLimit = failCount > limit;
        // 인증키도 받았고 failCount가 limt를 넘었을떄
        if (StringUtils.hasText(chptchaKey) && isFailLimit) {

            if (!StringUtils.hasText(captcha)) {
                throw new CaptchaMisMatchFactorException("login.error.captcha");
            }
            // 2차 인증 키 검증
            Map result = null;
            result = apiExamCaptchaNkeyService.getApiExamCaptchaNkeyResult(chptchaKey,
                captcha);
            isCaptcha = (boolean) result.get("result");
            // 번호가 맞지 않은 경우
            log.info("isCaptcha = {}", isCaptcha);
            log.info("result = {}", result);

            if (!isCaptcha) { // 인증키가 일치하지 않는 경우 다시 발급
                throw new CaptchaMisMatchFactorException("login.error.captcha");
            }
        }
        // 2차인증키가 있고 failCount가 5를 넘는 경우 captcha 검증

        // 로그인 실패가 5번 이상 일 경우 그리고 2차인증을 성공하지 못했거나 발급이 안 됐을 경우
        if (isFailLimit && !isCaptcha) {
            throw new CaptchaMisMatchFactorException("login.error");
        }
        return isCaptcha;
    }


    @Transactional
    public void loginSuccess(Long failCount,
        UserLoginFormDto userLoginFormDto) {

        String imageName = userLoginFormDto.getImageName();
        if (StringUtils.hasText(imageName)) { // 성공시 삭제
            try {
                apiExamCaptchaNkeyService.deleteImage(imageName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // 로그인 실패 기록 다 삭제
        if (failCount > 0) {
            userLoginLogService.delete(userLoginFormDto.getUserId(), LoginSuccess.FAIL, Status.OFF);
        }
    }


    @Transactional
    public void saveLoginLog(String userAgent, String userId, LoginSuccess success) {
        //로그인 성공 기록 저장
        userLoginLogService.save(userAgent, userId, success,
            Status.ON);
    }

    // 로그인 상태 저장
    @Transactional
    public void saveLoginStatus(String userAgent, JwtTokenDto jwtTokenDto, UserDetails userDetails, String sessionId) {
        try {
            userLoginStatusService.save(userAgent, jwtTokenDto, userDetails, sessionId);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("saveLoginStatus: " + e.getMessage());
        }
    }


}


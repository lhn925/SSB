package sky.Sss.domain.user.service.login;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import sky.Sss.domain.user.dto.login.CustomUserDetails;
import sky.Sss.domain.user.dto.login.LoginSuccessTokenDto;
import sky.Sss.domain.user.dto.login.UserLoginFormDto;
import sky.Sss.domain.user.exception.CaptchaMisMatchFactorException;
import sky.Sss.domain.user.exception.LoginBlockException;
import sky.Sss.domain.user.exception.RefreshTokenNotFoundException;
import sky.Sss.domain.user.exception.UserInfoNotFoundException;
import sky.Sss.domain.user.model.LoginSuccess;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.log.UserLoginLogService;
import sky.Sss.domain.user.utili.jwt.AccessTokenDto;
import sky.Sss.domain.user.utili.jwt.JwtTokenDto;
import sky.Sss.domain.user.utili.jwt.TokenProvider;
import sky.Sss.global.openapi.service.ApiExamCaptchaNkeyService;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class UserLoginService {

    private final UserLoginLogService userLoginLogService;
    private final ApiExamCaptchaNkeyService apiExamCaptchaNkeyService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserLoginStatusService userLoginStatusService;
    private final MessageSource ms;
    private final TokenProvider tokenProvider;


    @Transactional
    public ResponseEntity<AccessTokenDto> refreshActions(String refreshToken) {
        String Token = tokenProvider.resolveToken(refreshToken);
        Jws<Claims> claimsJws = tokenProvider.getRefreshClaimsJws(Token);
        try {
            // token validate
            userLoginStatusService.tokenValidate(refreshToken, claimsJws);
            // 새로운 accessToken 발급
            String accessToken = tokenProvider.recreationAccessToken(claimsJws);
            AccessTokenDto accessTokenDto = new AccessTokenDto();
            accessTokenDto.setAccessToken("Bearer " + accessToken);

            return new ResponseEntity<>(accessTokenDto, HttpStatus.OK);
        } catch (IOException e) {
            throw new RefreshTokenNotFoundException("refresh.error");
        }
    }
    @Transactional
    public ResponseEntity<?> loginActions(UserLoginFormDto userLoginFormDto, HttpServletRequest request,
        String userAgent, String captchaKey, HttpSession session) throws NoSuchFileException {
        LoginSuccess loginSuccess = LoginSuccess.FAIL;
        long limit = 4L; //
        long failCount = 0L;
        long uid = 0L;
        try {
            // 로그인 실패 횟수 조회
            failCount = userLoginLogService.getCount(userLoginFormDto.getUserId(), LoginSuccess.FAIL, Status.ON);

            // 2차 인증 코드 확인
            verifyCaptchKey(userLoginFormDto, failCount, captchaKey, limit);

            Authentication authenticate = login(userLoginFormDto, request.getHeader("User-Agent"),
                session.getId(), failCount);

            // 로그인 성공시 jwt Dto 생성
            JwtTokenDto jwtTokenDto = tokenProvider.createToken(authenticate);

            CustomUserDetails userDetails = (CustomUserDetails) authenticate.getPrincipal();

            // 해외 로그인 차단
            userLoginLogService.isLoginBlockChecked(userDetails.getLoginBlocked());

            uid = userDetails.getUId();
            // 로그인 성공 log 저장
            loginSuccess(failCount, userLoginFormDto);
            // status 저장
            saveLoginStatus(userAgent, jwtTokenDto, uid, userDetails.getUserId(),
                request.getSession().getId());

            loginSuccess = LoginSuccess.SUCCESS;

            LoginSuccessTokenDto successTokenDto = LoginSuccessTokenDto.createJwtTokenDto(jwtTokenDto.getAccessToken(),
                jwtTokenDto.getRefreshToken());
            return new ResponseEntity<>(successTokenDto, HttpStatus.OK);
        } catch (LoginBlockException e) {
            userLoginFormDto.setMessage(ms.getMessage(e.getMessage(), null, request.getLocale()));
            return new ResponseEntity<>(userLoginFormDto, HttpStatus.UNAUTHORIZED);
        } catch (UserInfoNotFoundException | BadCredentialsException | CaptchaMisMatchFactorException e) {
            failCount++;// 실패횟수 +
            if (failCount > limit || StringUtils.hasText(captchaKey)) {
                if (StringUtils.hasText(userLoginFormDto.getImageName())) {
                    apiExamCaptchaNkeyService.deleteImage(userLoginFormDto.getImageName());
                }
                Map<String, Object> mapKey = apiExamCaptchaNkeyService.getApiExamCaptchaNkey();
                String key = (String) mapKey.get("key");

                String image = apiExamCaptchaNkeyService.getApiExamCaptchaImage(key);

                userLoginFormDto.setCaptchaKey(key);
                userLoginFormDto.setImageName(image);
                String message = "login.error.captcha";
                userLoginFormDto.setMessage(ms.getMessage(message, null, request.getLocale()));
                return new ResponseEntity<>(userLoginFormDto, HttpStatus.UNAUTHORIZED);
            } else {
                return null;
            }
        } finally {
            saveLoginLog(userAgent, userLoginFormDto.getUserId(), uid, loginSuccess);
        }
    }

    @Transactional
    public Authentication login(UserLoginFormDto userLoginFormDto, String userAgent, String sessionId, Long failCount) {
        /**
         * 유저가 로그인 버튼을 입력한 URL 저장
         */
        String userId = userLoginFormDto.getUserId();
        String password = userLoginFormDto.getPassword();

        // 아이디 및 비번 확인 jwt 발급
        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(
            userId,
            password);
        // redisToken 미포함
        return authenticationManagerBuilder.getObject().authenticate(authRequest);
    }


    public boolean verifyCaptchKey(UserLoginFormDto userLoginFormDto, Long failCount, String chptchaKey, Long limit)
        throws CaptchaMisMatchFactorException {
        boolean isCaptcha = false; // 인증코드 성공 여부
        String captcha = userLoginFormDto.getCaptcha();
        boolean isFailLimit = failCount > limit;
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
    public void saveLoginLog(String userAgent, String userId, long uid, LoginSuccess success) {
        //로그인 성공 기록 저장
        userLoginLogService.add(userAgent, userId, uid, success,
            Status.ON);
    }

    // 로그인 상태 저장
    @Transactional
    public void saveLoginStatus(String userAgent, JwtTokenDto jwtTokenDto, long uid, String userId, String sessionId) {
        try {
            userLoginStatusService.add(userAgent, jwtTokenDto, userId, uid, sessionId);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("saveLoginStatus: " + e.getMessage());
        }
    }
}


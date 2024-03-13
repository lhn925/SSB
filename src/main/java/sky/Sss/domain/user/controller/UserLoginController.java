package sky.Sss.domain.user.controller;


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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.user.dto.login.CustomUserDetails;
import sky.Sss.domain.user.dto.login.UserLoginFormDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.exception.CaptchaMisMatchFactorException;
import sky.Sss.domain.user.exception.LoginBlockException;
import sky.Sss.domain.user.exception.LoginFailException;
import sky.Sss.domain.user.exception.RefreshTokenNotFoundException;
import sky.Sss.domain.user.exception.UserInfoNotFoundException;
import sky.Sss.domain.user.model.LoginSuccess;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.domain.user.service.log.UserLoginLogService;
import sky.Sss.domain.user.service.login.UserLoginService;
import sky.Sss.domain.user.service.login.UserLoginStatusService;
import sky.Sss.domain.user.utili.jwt.AccessTokenDto;
import sky.Sss.domain.user.utili.jwt.JwtDto;
import sky.Sss.domain.user.utili.jwt.JwtFilter;
import sky.Sss.domain.user.utili.jwt.JwtTokenDto;
import sky.Sss.domain.user.utili.jwt.TokenProvider;
import sky.Sss.global.openapi.service.ApiExamCaptchaNkeyService;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/login")
@RestController
public class UserLoginController {

    private final MessageSource ms;
    private final UserLoginService userLoginService;
    private final UserLoginLogService userLoginLogService;
    private final TokenProvider tokenProvider;
    private final ApiExamCaptchaNkeyService apiExamCaptchaNkeyService;
    private final UserLoginStatusService userLoginStatusService;
    /**
     * @param request
     * @return
     */

    @PostMapping
    public ResponseEntity<?> login(@Validated @RequestBody UserLoginFormDto userLoginFormDto,
        BindingResult bindingResult,
        HttpServletRequest request) throws UsernameNotFoundException, NoSuchFileException {
        if (bindingResult.hasErrors()) {
            throw new LoginFailException("login.error");
        }
        String userAgent = request.getHeader("User-Agent");
        String captchaKey = userLoginFormDto.getCaptchaKey();
        HttpSession session = request.getSession();
        LoginSuccess loginSuccess = LoginSuccess.FAIL;
        long limit = 4L; //
        long failCount = 0L;
        long uid = 0L;
        try {
            // 로그인 실패 횟수 조회
            failCount = userLoginLogService.getCount(userLoginFormDto.getUserId(), LoginSuccess.FAIL, Status.ON);

            // 2차 인증 코드 확인
            userLoginService.verifyCaptchKey(userLoginFormDto, failCount, captchaKey, limit);

            Authentication authenticate = userLoginService.login(userLoginFormDto, request.getHeader("User-Agent"),
                session.getId(), failCount);

            // 로그인 성공시 jwt Dto 생성
            JwtTokenDto jwtTokenDto = tokenProvider.createToken(authenticate);

            CustomUserDetails userDetails = (CustomUserDetails) authenticate.getPrincipal();

            // 해외 로그인 차단
            userLoginLogService.isLoginBlockChecked(userDetails.getLoginBlocked());

            uid = userDetails.getUId();
            // 로그인 성공 log 저장
            userLoginService.loginSuccess(failCount, userLoginFormDto);
            // status 저장
            userLoginService.saveLoginStatus(userAgent, jwtTokenDto, uid, userDetails.getUserId(),
                request.getSession().getId());

            loginSuccess = LoginSuccess.SUCCESS;
            return new ResponseEntity<>(jwtTokenDto, HttpStatus.OK);
        } catch (LoginBlockException e) {
            userLoginFormDto.setMessage(ms.getMessage(e.getMessage(), null, request.getLocale()));
            return new ResponseEntity<>(userLoginFormDto, HttpStatus.UNAUTHORIZED);
        } catch (UserInfoNotFoundException | BadCredentialsException | CaptchaMisMatchFactorException e) {
            failCount++;// 실패횟수 +
            if (failCount > limit || StringUtils.hasText(captchaKey)) {
                if (StringUtils.hasText(userLoginFormDto.getImageName())) {
                    apiExamCaptchaNkeyService.deleteImage(userLoginFormDto.getImageName());
                }
                Map mapKey = apiExamCaptchaNkeyService.getApiExamCaptchaNkey();
                String key = (String) mapKey.get("key");

                String image = apiExamCaptchaNkeyService.getApiExamCaptchaImage(key);

                userLoginFormDto.setCaptchaKey(key);
                userLoginFormDto.setImageName(image);
                String message = "login.error.captcha";
                userLoginFormDto.setMessage(ms.getMessage(message, null, request.getLocale()));
                return new ResponseEntity<>(userLoginFormDto, HttpStatus.UNAUTHORIZED);
            } else {
                throw new BadCredentialsException("login.error");
            }
        } finally {
            userLoginService.saveLoginLog(userAgent, userLoginFormDto.getUserId(), uid, loginSuccess);
        }
    }

    /**
     * @param request
     * @return
     */
    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenDto> refresh(HttpServletRequest request) {
        String refreshToken = request.getHeader(JwtFilter.AUTHORIZATION_HEADER);
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

    /**
     *
     */
    @GetMapping("/check")
    public ResponseEntity<?> loginCheck(HttpServletRequest request) {
        log.info("request.getSession().getId() = {}", request.getSession().getId());
        return new ResponseEntity("안녕", HttpStatus.OK);
    }
}

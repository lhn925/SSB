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
import sky.Sss.domain.user.dto.login.LoginSuccessTokenDto;
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

    private final UserLoginService userLoginService;
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
        ResponseEntity<?> responseEntity = userLoginService.loginActions(userLoginFormDto, request, userAgent,
            captchaKey, session);
        if (responseEntity == null) {
            throw new BadCredentialsException("login.error");
        }
        return responseEntity;
    }
    /**
     * @param request
     * @return
     */
    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenDto> refresh(HttpServletRequest request) {
        String refreshToken = request.getHeader(JwtFilter.AUTHORIZATION_HEADER);
        return userLoginService.refreshActions(refreshToken);
    }




}

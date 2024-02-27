package sky.Sss.domain.user.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.user.annotation.UserAuthorize;
import sky.Sss.domain.user.service.login.UserLogoutService;
import sky.Sss.domain.user.utili.jwt.JwtFilter;
import sky.Sss.domain.user.utili.jwt.TokenProvider;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/logout")
public class UserLogoutController {


    private final TokenProvider tokenProvider;
    private final UserLogoutService userLogoutService;

    @UserAuthorize
    @PostMapping
    public ResponseEntity logout(HttpServletRequest request) {

        try {
            String accessToken = tokenProvider.resolveToken(request.getHeader(JwtFilter.AUTHORIZATION_HEADER));
            String refreshToken = tokenProvider.resolveToken(request.getHeader(JwtFilter.REFRESH_AUTHORIZATION_HEADER));

            userLogoutService.logout(accessToken, refreshToken);

            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
        } catch (AuthenticationException e) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

}

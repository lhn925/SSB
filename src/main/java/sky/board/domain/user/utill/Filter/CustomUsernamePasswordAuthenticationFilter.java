package sky.board.domain.user.utill.Filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sky.board.domain.user.utill.CustomUsernamePasswordAuthenticationToken;

@Slf4j
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private boolean postOnly = true;


    public CustomUsernamePasswordAuthenticationFilter(
        AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        log.info("안녕하세요 여기는");
        /**
         * 유저가 로그인 버튼을 입력한 URL 저장
         */
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        String username = request.getParameter("userId");
        username = (username != null) ? username.trim() : "";

        String password = request.getParameter("password");
        password = (password != null) ? password : "";

        String url = request.getParameter("url");
        url = (url != null) ? url : "";

        log.info("username {}",username);
        log.info("password {}",password);
        log.info("url {}",url);

        CustomUsernamePasswordAuthenticationToken authRequest =
            CustomUsernamePasswordAuthenticationToken.unauthenticated(
                url, username,
                password
            );
        // Allow subclasses to set the "details" property
        this.setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    @Override
    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        this.setDetails(request, authRequest);
    }
    protected void setDetails(HttpServletRequest request, CustomUsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

}

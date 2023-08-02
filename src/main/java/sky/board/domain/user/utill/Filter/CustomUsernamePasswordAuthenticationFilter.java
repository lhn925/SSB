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
        String username = obtainUsername(request);
        username = (username != null) ? username.trim() : "";

        String password = obtainPassword(request);
        password = (password != null) ? password : "";

        String url = request.getParameter("url");
        url = (url != null) ? url : "";

        CustomUsernamePasswordAuthenticationToken authRequest =
            CustomUsernamePasswordAuthenticationToken.unauthenticated(
                url, username,
                password
            );
        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return super.getAuthenticationManager().authenticate(authRequest);
    }


}

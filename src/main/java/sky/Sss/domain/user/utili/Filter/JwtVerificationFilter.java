package sky.Sss.domain.user.utili.Filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import sky.Sss.domain.user.dto.UserInfoDto;
import sky.Sss.domain.user.exception.UserInfoNotFoundException;
import sky.Sss.global.error.dto.ErrorGlobalResultDto;
import sky.Sss.global.error.dto.ErrorResult;
import sky.Sss.global.error.dto.Result;
import sky.Sss.global.redis.dto.RedisKeyDto;

@Slf4j
public class JwtVerificationFilter extends OncePerRequestFilter {


    private final RequestMatcher uriMatcher;
    private final MessageSource ms;

    public JwtVerificationFilter(String uriMatcher, MessageSource ms) {
        this.uriMatcher = new AntPathRequestMatcher(uriMatcher);
        this.ms = ms;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        log.info("ApiKeyAuthFilter");
        HttpSession session = request.getSession(false);

        try {
            UserInfoDto userInfoDto = (UserInfoDto) session.getAttribute(RedisKeyDto.REDIS_USER_KEY);
            if (userInfoDto == null) {
                throw new UserInfoNotFoundException();
            }
            filterChain.doFilter(request, response);
        } catch (NullPointerException | UserInfoNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            ResponseEntity<ErrorResult> errorResult = Result.getErrorResult(
                new ErrorGlobalResultDto("login.error.unAuth", ms, request.getLocale()), HttpStatus.FORBIDDEN);
            String json = new ObjectMapper().writeValueAsString(errorResult);
            log.info("json = {}", json);
            response.getWriter().write(json);
        }

        ///users/myInfo/api/picture/4a2122bd-772e-4edd-bba9-10d4f53ceabd.jpg
        ///users/myInfo/api/picture/003a0a7c-c4a1-43ad-9ef6-e5d79692a88d.jpg
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        RequestMatcher matcher = new NegatedRequestMatcher(uriMatcher);
        return matcher.matches(request);
    }
}

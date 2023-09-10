package sky.board.domain.user.utili.Filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import sky.board.domain.user.dto.UserInfoDto;
import sky.board.domain.user.exception.UserInfoNotFoundException;
import sky.board.global.error.dto.ErrorGlobalResultDto;
import sky.board.global.error.dto.ErrorResult;
import sky.board.global.error.dto.Result;
import sky.board.global.redis.dto.RedisKeyDto;

@Slf4j
public class ApiKeyAuthFilter extends OncePerRequestFilter {


    private final RequestMatcher uriMatcher;
    private final MessageSource ms;

    public ApiKeyAuthFilter(String uriMatcher, MessageSource ms) {
        this.uriMatcher = new AntPathRequestMatcher(uriMatcher);
        this.ms = ms;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        log.info("ApiKeyAuthFilter");
        UserInfoDto userInfoDto = (UserInfoDto) request.getSession().getAttribute(RedisKeyDto.USER_KEY);

        try {
            if (userInfoDto == null) {
                throw new UserInfoNotFoundException("error.unAuth");
            }
            filterChain.doFilter(request, response);
        } catch (UserInfoNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            ResponseEntity<ErrorResult> errorResult = Result.getErrorResult(
                new ErrorGlobalResultDto(e.getMessage(), ms, request.getLocale()), HttpStatus.FORBIDDEN);
            String json = new ObjectMapper().writeValueAsString(errorResult);
            log.info("json = {}", json);
            response.getWriter().write(json);
        }

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        RequestMatcher matcher = new NegatedRequestMatcher(uriMatcher);
        return matcher.matches(request);
    }
}

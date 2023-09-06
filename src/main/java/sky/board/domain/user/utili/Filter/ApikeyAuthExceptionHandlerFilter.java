package sky.board.domain.user.utili.Filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.web.filter.OncePerRequestFilter;
import sky.board.domain.user.exception.UserInfoNotFoundException;
import sky.board.global.utili.Alert;

/**
 *
 * ApikeyFilter 에서 예외발생시 처리 filter
 */
@Slf4j
public class ApikeyAuthExceptionHandlerFilter extends OncePerRequestFilter {
    private final MessageSource ms;
    public ApikeyAuthExceptionHandlerFilter(MessageSource ms) {
        this.ms = ms;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        try {
            log.info("ApikeyAuthExceptionHandlerFilter");
            filterChain.doFilter(request,response);
        } catch (UserInfoNotFoundException e) {
            log.info("e.getMessage() = {}", e.getMessage());
//            Alert.waringAlert(ms.getMessage(e.getMessage(), null, request.getLocale()),"/login",response);
            response.sendRedirect(request.getContextPath()+"/login");
        }

    }
}

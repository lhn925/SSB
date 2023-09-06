package sky.board.domain.user.utili.Filter;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import sky.board.domain.user.dto.UserInfoDto;
import sky.board.domain.user.exception.UserInfoNotFoundException;
import sky.board.global.redis.dto.RedisKeyDto;

public class ApiKeyAuthFilter extends AbstractPreAuthenticatedProcessingFilter {


    public ApiKeyAuthFilter() {
    }
    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        UserInfoDto userInfo = (UserInfoDto) request.getSession().getAttribute(RedisKeyDto.USER_KEY);
        if (userInfo == null) {
             throw new UserInfoNotFoundException("error.unAuth");
        }
        return userInfo;
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }
}

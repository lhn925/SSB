package sky.Sss.global.ws.utili.handler;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import sky.Sss.domain.user.service.login.UserLoginStatusService;
import sky.Sss.domain.user.utili.jwt.JwtFilter;
import sky.Sss.domain.user.utili.jwt.TokenProvider;

/**
 *
 * socket handler
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 99) // 가장 높은 우선순위로 설정
@RequiredArgsConstructor
@Component
public class WebSocketSessionHandler implements ChannelInterceptor {

    private final UserLoginStatusService userLoginStatusService;
    private final TokenProvider tokenProvider;
    private String redisToken;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (accessor.getCommand().getMessageType().equals(SimpMessageType.CONNECT)) {
            Authentication authentication = getAuthByAuthorizationHeader(accessor);
            Optional.ofNullable(authentication).orElseThrow(() -> {
                throw new MessageDeliveryException("JWT");
            });
            UserDetails principal = (UserDetails) authentication.getPrincipal();
            userLoginStatusService.wsIdUpdate(principal.getUsername(), redisToken, accessor.getSessionId());
        }
        return message;
    }

    public Authentication getAuthByAuthorizationHeader(StompHeaderAccessor accessor) {
        List<String> authorization = accessor.getNativeHeader(JwtFilter.AUTHORIZATION_HEADER);
        if (authorization != null && authorization.size() != 0) {
            String accessToken = tokenProvider.resolveToken(authorization.get(0));
            Boolean success = (Boolean) tokenProvider.validateAccessToken(accessToken).get("success");
            if (success) { // redisToken
                Jws<Claims> accessClaimsJws = tokenProvider.getAccessClaimsJws(accessToken);
                this.redisToken = (String) accessClaimsJws.getBody().get(TokenProvider.REDIS_TOKEN_KEY);
            }
            return success ? tokenProvider.getAuthentication(accessToken) : null;
        }
        return null;
    }


}

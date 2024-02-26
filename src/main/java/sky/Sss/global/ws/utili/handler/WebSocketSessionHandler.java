package sky.Sss.global.ws.utili.handler;

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
import sky.Sss.domain.user.model.Enabled;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.domain.user.utili.jwt.TokenProvider;
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisCacheService;

/**
 * socket handler
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 99) // 가장 높은 우선순위로 설정
@RequiredArgsConstructor
@Component
public class WebSocketSessionHandler implements ChannelInterceptor {

    private final TokenProvider tokenProvider;
    private final RedisCacheService redisCacheService;
    private final UserQueryService userQueryService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        SimpMessageType messageType = accessor.getCommand().getMessageType();
        String sessionId = accessor.getSessionId();

        if (messageType.equals(SimpMessageType.CONNECT)) {
            Authentication authentication = tokenProvider.getAuthByAuthorizationHeader(accessor);
            Optional.ofNullable(authentication).orElseThrow(() -> {
                throw new MessageDeliveryException("JWT");
            });

            /**
             *
             * userId로 userToken 을 얻은 후
             * Redis에 저장된 WebSocketKey + userToken에
             * webSocket key 를 저장 혹은 삭제
             *
             */
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String userId = userDetails.getUsername();
            String userToken = userQueryService.getToken(userId, Enabled.ENABLED);
            /**
             * redis cache 저장
             * RedisKeyDto.REDIS_USER_WS_LIST_SESSION_KEY + userToken
             * set 으로 저장 : {key1,key2,...}
             *
             */

            redisCacheService.setData(RedisKeyDto.REDIS_WS_SESSION_KEY + sessionId, sessionId);
            redisCacheService.upsertCacheSetValue(sessionId, RedisKeyDto.REDIS_USER_WS_LIST_SESSION_KEY + userToken);
//            log.info("Received a new web socket connection. Session ID : [{}]", headerAccessor.getSessionId());
        }
        if (messageType.equals(SimpMessageType.DISCONNECT)) {
            redisCacheService.delete(RedisKeyDto.REDIS_WS_SESSION_KEY + sessionId);
        }
        /*
        [payload=byte[0], headers={simpMessageType=SUBSCRIBE, stompCommand=SUBSCRIBE,
        nativeHeaders={id=[sub-1], destination=[/user/queue/push/msg]}, simpSessionAttributes={}, simpHeartbeat=[J@4208bf42, simpSubscriptionId=sub-1, simpSessionId=da8359f2-4d52-8adf-3dad-4a7680a030b1, simpDestination=/user/queue/push/msg}]
         */
        return message;
    }


}

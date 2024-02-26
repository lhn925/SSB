package sky.Sss.global.ws.utili.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import sky.Sss.domain.user.model.Enabled;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.domain.user.utili.jwt.JwtFilter;
import sky.Sss.domain.user.utili.jwt.TokenProvider;
import sky.Sss.domain.user.utili.listener.entitiy.BrowserSession;
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisCacheService;
import sky.Sss.global.redis.service.RedisQueryService;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private static Map<String, BrowserSession> browserSessionMap = new ConcurrentHashMap<>();
    /**
     * Handle session connected events.
     *
     * @param event the event
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("event.getMessage() = {}", event.getMessage());
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        log.info("Received a new web socket connection. Session ID : [{}]", headerAccessor.getSessionId());

    }
    /**
     * Handle session disconnected events.
     *
     * @param event the event
     */
    @EventListener
    public void handleWebSocketDisConnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = findBrowserSessionId(headerAccessor.getSessionId());
        if(sessionId != null) {
            browserSessionMap.remove(headerAccessor.getSessionId());
        }
        log.info("Web socket session closed. Message : [{}]", event.getMessage());
    }

    /**
     * Find session id by session id.
     *
     * @param sessionId
     * @return
     */
    public String findBrowserSessionId(String sessionId) {
        String session = null;

        for (Map.Entry<String, BrowserSession> entry : browserSessionMap.entrySet()) {
            if (entry.getKey().equals(sessionId)) {
                session = entry.getKey();
            }
        }
        return session;
    }

    /**
     * Register browser session.
     *
     * @param browserSession the browser session
     * @param sessionId      the session id
     */
    public synchronized void registerBrowserSession(BrowserSession browserSession, String sessionId) {
        browserSessionMap.put(sessionId, browserSession);
    }

    /**
     * Find session ids by user name list.
     *
     * @param username the member id
     * @return the list
     */
    public List<String> findSessionIdsByMemberId(String username) {
        List<String> sessionIdList = new ArrayList();

        for (Map.Entry<String, BrowserSession> entry : browserSessionMap.entrySet()) {
            if (entry.getValue().getUserId().equals(username)) {
                sessionIdList.add(entry.getKey());
            }
        }
        return sessionIdList;
    }

    /**
     * Create headers message headers.
     *
     * @param sessionId the session id
     * @return the message headers
     */
    public MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);

        return headerAccessor.getMessageHeaders();
    }

}

package sky.Sss.domain.user.utili.handler.webSocket;

import java.lang.reflect.Type;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;
import sky.Sss.domain.user.dto.HelloMessage;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompSessionHandler extends StompSessionHandlerAdapter {
    private StompSession stompSession;
    /**
     * 접속시
     * @param session the client STOMP session
     * @param connectedHeaders the STOMP CONNECTED frame headers
     */
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        log.info("afterConnected session.getSessionId() = {}", session.getSessionId());
        this.stompSession = session;
        subscribe("/login");
    }
    @Override
    public Type getPayloadType(StompHeaders headers) {
        return HelloMessage.class;
    }
    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        HelloMessage msg = (HelloMessage) payload;
    }
    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
        Throwable exception) {
        log.info("handleException exception = {}", exception.getMessage());
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {

        log.info(" handleTransportError xception.getMessage() = {}", exception.getMessage());
    }

    /**
     * Subscribe.
     *
     * @param destination the destination
     */
    public synchronized void subscribe(String destination) {
        stompSession.subscribe(destination, this);
    }
}

package sky.Sss.domain.user.utili.handler.webSocket;

import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Slf4j
@Component
public class WebSocketErrorHandler extends StompSubProtocolErrorHandler {


    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        if (ex.getMessage().equals("JWT")) {
            return handleJwtException();
        }
        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    // JWT 예외
    private Message<byte[]> handleJwtException () {
        return prepareErrorMessage();
    }

    private Message<byte[]> prepareErrorMessage() {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
        String code = "JWT";
        headerAccessor.setMessage(code);
        headerAccessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(code.getBytes(StandardCharsets.UTF_8),headerAccessor.getMessageHeaders());
    }


}

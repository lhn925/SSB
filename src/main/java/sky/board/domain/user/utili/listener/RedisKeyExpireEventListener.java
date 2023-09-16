package sky.board.domain.user.utili.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.session.events.SessionCreatedEvent;
import org.springframework.session.events.SessionDestroyedEvent;
import org.springframework.session.events.SessionExpiredEvent;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class RedisSessionListener extends KeyExpirationEventMessageListener {

    public RedisSessionListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("message = {}", message.toString());
        log.info("new String(pattern) = {}", new String(pattern));


        super.onMessage(message, pattern);
    }
}

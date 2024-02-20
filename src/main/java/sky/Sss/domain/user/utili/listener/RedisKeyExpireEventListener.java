package sky.Sss.domain.user.utili.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.login.UserLoginStatusService;
import sky.Sss.global.redis.dto.RedisKeyDto;


@Slf4j
@Component
public class RedisKeyExpireEventListener extends KeyExpirationEventMessageListener {

    private final UserLoginStatusService userLoginStatusService;

    public RedisKeyExpireEventListener(RedisMessageListenerContainer listenerContainer,
        UserLoginStatusService userLoginStatusService) {
        super(listenerContainer);
        this.userLoginStatusService = userLoginStatusService;
    }



    /**
     * Redis 세션 키 만료시 login 세션 기기 상태 변경
     * @param message message must not be {@literal null}.
     * @param pattern pattern matching the channel (if specified) - can be {@literal null}.
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("message = {}", message.toString());
        log.info("new String(pattern) = {}", new String(pattern));

        String session = message.toString();
        expireLoginStatusUpdate(session);
        super.onMessage(message, pattern);
    }

    /**
     * 레디스 데이터가 expire 시 redis키 타입 파악 후 해당 로그인 상태를 off로 업데이트
     * @param session
     */
    public void expireLoginStatusUpdate (String session) {

        boolean isSession = session.contains(RedisKeyDto.SESSION_KEY);
        int index = 0;
        String key = "";
        if (isSession) {
            index = session.lastIndexOf(":");
            key = session.substring(index+1);
            userLoginStatusService.expireRedisSessionKeyOff(key, Status.OFF,Status.OFF);
        }
    }


}

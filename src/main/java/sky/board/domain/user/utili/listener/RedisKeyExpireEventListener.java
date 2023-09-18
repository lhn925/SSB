package sky.board.domain.user.utili.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring6.context.SpringContextUtils;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.service.login.UserLoginStatusService;
import sky.board.global.redis.dto.RedisKeyDto;


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
        boolean isRemember = session.contains(RedisKeyDto.REMEMBER_KEY);

        boolean isLoginStatus = isSession || isRemember;

        int index = 0;
        String key = "";
        if (isLoginStatus){
            index = session.lastIndexOf(":");
            key = session.substring(index+1);
        }
        if (isRemember) {
            userLoginStatusService.expireRedisRememberKeyOff(key, Status.OFF,Status.OFF);
        } else if (isSession) {
            userLoginStatusService.expireRedisSessionKeyOff(key, Status.OFF,Status.OFF);
        }
    }


}

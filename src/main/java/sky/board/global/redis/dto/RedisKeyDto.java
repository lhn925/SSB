package sky.board.global.redis.dto;

import org.springframework.beans.factory.annotation.Value;

public class RedisKeyDto {
    @Value("${spring.session.redis.namespace}" + ":sessions:")
    public static String SESSION_KEY;

    public static final String USER_KEY = "USER_ID";
}

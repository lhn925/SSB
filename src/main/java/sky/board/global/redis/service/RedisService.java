package sky.board.global.redis.service;

import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import sky.board.global.redis.dto.RedisKeyDto;

@Slf4j
@Component
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setData(String key, Object value, Long expiredTime) {
        redisTemplate.opsForValue().set(key, value, expiredTime, TimeUnit.MILLISECONDS);
    }

    public void setRememberData(String key, String value, Long expiredTime) {
        redisTemplate.opsForValue().set(RedisKeyDto.REMEMBER_KEY + key, value, expiredTime, TimeUnit.MILLISECONDS);
    }

    public String getData(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    public String getRememberData(String key) {
        return (String) redisTemplate.opsForValue().get(RedisKeyDto.REMEMBER_KEY + key);
    }

    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    public Boolean deleteRemember(String key) {
        return redisTemplate.delete(RedisKeyDto.REMEMBER_KEY + key);
    }

    public Boolean deleteSession(String key) {
        return redisTemplate.delete(RedisKeyDto.SESSION_KEY + key);
    }


    public Boolean hasRedis(String key) {
        return redisTemplate.hasKey(key);
    }


}

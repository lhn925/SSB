package sky.board.global.redis.service;

import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.CacheProperties.Redis;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import sky.board.global.redis.dto.RedisKeyDto;

@Slf4j
@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setData(String key, Object value, Long expiredTime) {
        redisTemplate.opsForValue().set(key, value, expiredTime, TimeUnit.MILLISECONDS);
    }

    public void setRememberData(String key, Object value, Long expiredTime) {
        redisTemplate.opsForValue().set(RedisKeyDto.REMEMBER_KEY + key, value, expiredTime, TimeUnit.MILLISECONDS);
    }

    public void setSessionData(String key, Object value, Long expiredTime) {
        redisTemplate.opsForValue().set(RedisKeyDto.SESSION_KEY + key, value, expiredTime, TimeUnit.MILLISECONDS);
    }


    public String getData(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    public String getRememberData(String key) {
        return (String) redisTemplate.opsForValue().get(RedisKeyDto.REMEMBER_KEY + key);
    }

    public String getSessionData(String key) {
        return (String) redisTemplate.opsForValue().get(RedisKeyDto.SESSION_KEY + key);
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

    public void sessionDeleteData(String key) {
        redisTemplate.delete(RedisKeyDto.SESSION_KEY + key);
    }

    public void rememberDeleteData(String key) {
        redisTemplate.delete(RedisKeyDto.REMEMBER_KEY + key);
    }


}

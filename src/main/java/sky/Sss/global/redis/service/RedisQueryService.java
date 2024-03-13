package sky.Sss.global.redis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import sky.Sss.global.redis.dto.RedisKeyDto;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisQueryService {


    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;


    public void setData(String key, Object value, Long expiredTime) {
        redisTemplate.opsForValue().set(key, value, expiredTime, TimeUnit.MILLISECONDS);
    }

    public void setData(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }


    public void setRememberData(String key, String value, Long expiredTime) {
        redisTemplate.opsForValue().set(RedisKeyDto.REDIS_REMEMBER_KEY + key, value, expiredTime, TimeUnit.MILLISECONDS);
    }

    public String getData(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    public <T> T getData(String key, TypeReference<T> typeReference) {
        try {
            // typeReference 를 사용 하여 json 문자열을 TypeReference 선언된 제네릭에 맞게 역 직렬화
            return objectMapper.readValue((String) redisTemplate.opsForValue().get(key), typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getRememberData(String key) {
        return (String) redisTemplate.opsForValue().get(RedisKeyDto.REDIS_REMEMBER_KEY + key);
    }

    public Boolean delete(String key) {

        return redisTemplate.delete(key);
    }

    public Boolean deleteRemember(String key) {
        return redisTemplate.delete(RedisKeyDto.REDIS_REMEMBER_KEY + key);
    }

    public Boolean deleteSession(String key) {
        return redisTemplate.delete(RedisKeyDto.REDIS_SESSION_KEY + key);
    }


    public Boolean hasRedis(String key) {
        if (!StringUtils.hasText(key)) {
            return false;
        }
        return redisTemplate.hasKey(key);
    }


}

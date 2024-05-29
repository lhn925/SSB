package sky.Sss.global.redis.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import sky.Sss.global.redis.dto.RedisDataListDto;
import sky.Sss.global.redis.dto.RedisKeyDto;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisQueryService {


    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;


    public void setData(String key, Object value, Long expiredTime) {
        try {
            redisTemplate.opsForValue().set(key, value, expiredTime, TimeUnit.MILLISECONDS);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failed", e);
        } catch (Exception e) {
            log.error("Error saving data to Redis", e);
        }
    }

    public void setData(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failed", e);
        } catch (Exception e) {
            log.error("Error saving data to Redis", e);
        }

    }


    public void setRememberData(String key, String value, Long expiredTime) {
        try {
            redisTemplate.opsForValue()
                .set(RedisKeyDto.REDIS_REMEMBER_KEY + key, value, expiredTime, TimeUnit.MILLISECONDS);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failed", e);
        } catch (Exception e) {
            log.error("Error saving data to Redis", e);
        }
    }

    public String getData(String key) {

        try {
            return (String) redisTemplate.opsForValue().get(key);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failed", e);
            return null;
        } catch (Exception e) {
            log.error("Error saving data to Redis", e);
            return null;
        }
    }

    public <T> T getData(String key, TypeReference<T> typeReference) {
//
        try {
            return objectMapper.readValue((String) redisTemplate.opsForValue().get(key), typeReference);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failed", e);
            return null;
        } catch (Exception e) {
            log.error("Error saving data to Redis", e);
            return null;
        }
    }


    public <T> RedisDataListDto<T> getDataList(List<String> keys, TypeReference<T> typeReference) {
        try {
            List<Object> cachedData = redisTemplate.opsForValue().multiGet(keys);
            Map<String, T> result = new HashMap<>();
            List<String> missingKeys = new ArrayList<>();
            // redisKey 값 삭제 후 온전한 토큰 추출
            int removeIndex = keys.get(0).lastIndexOf(":");
            for (int i = 0; i < keys.size(); i++) {
                String data = (String) cachedData.get(i);
                String key = keys.get(i).substring(removeIndex + 1);
                if (data != null) {
                    JavaType javaType = TypeFactory.defaultInstance().constructType(typeReference);
                    T value = objectMapper.readValue(data, javaType);
                    result.put(key, value);
                } else {
                    missingKeys.add(key);
                }
            }
            return new RedisDataListDto<T>(result, missingKeys);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failed", e);
            return null;
        } catch (Exception e) {
            log.error("Error saving data to Redis", e);
            return null;
        }
    }


    public String getRememberData(String key) {

        try {
            return (String) redisTemplate.opsForValue().get(RedisKeyDto.REDIS_REMEMBER_KEY + key);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failed", e);
            return null;
        } catch (Exception e) {
            log.error("Error saving data to Redis", e);
            return null;
        }

    }

    public Boolean delete(String key) {
        try {
            return redisTemplate.delete(key);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failed", e);
            return true;
        } catch (Exception e) {
            log.error("Error saving data to Redis", e);
            return true;
        }

    }


    public Boolean deleteRemember(String key) {

        try {
            return redisTemplate.delete(RedisKeyDto.REDIS_REMEMBER_KEY + key);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failed", e);
            return true;
        } catch (Exception e) {
            log.error("Error saving data to Redis", e);
            return true;
        }
    }

    public Boolean deleteSession(String key) {

        try {
            return redisTemplate.delete(RedisKeyDto.REDIS_SESSION_KEY + key);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failed", e);
            return true;
        } catch (Exception e) {
            log.error("Error saving data to Redis", e);
            return true;
        }

    }


    public Boolean hasRedis(String key) {
        try {
            if (!StringUtils.hasText(key)) {
                return false;
            }
            return redisTemplate.hasKey(key);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failed", e);
            return false;
        } catch (Exception e) {
            log.error("Error saving data to Redis", e);
            return false;
        }
    }


}

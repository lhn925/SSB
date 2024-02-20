package sky.Sss.global.redis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import sky.Sss.domain.user.dto.UserSimpleInfoDto;
import sky.Sss.domain.user.entity.User;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisCacheService {


    private final ObjectMapper objectMapper;
    private final RedisQueryService redisQueryService;

    public void setData(String key, Object value) {
        redisQueryService.setData(key, value);
    }


    public <T> T getData(String key, TypeReference<T> typeReference) {
        return redisQueryService.getData(key, typeReference);
    }

    public Boolean delete(String key) {
        return redisQueryService.delete(key);
    }

    public Boolean hasRedis(String key) {
        return redisQueryService.hasRedis(key);
    }

    /**
     * track,ply,reply 의 like 횟수를 가져오는 Method
     * @param key
     * @param token
     * @return
     */
    public Integer getCount(String key, String token) {
        TypeReference<HashMap<String, Integer>> typeReference = new TypeReference<>() {};
        HashMap<String, Integer> map;
        Integer count = null;
        if (hasRedis(key)) {
            map = getData(key, typeReference);
            count = map.get(token);
        }
        return count;
    }

    /**
     * map 형태로 된 cache 에 값이 존재하는 지 boolean 값으로 반환 검색
     * @param user
     * @param key
     * @return
     */
    public boolean existsByUserId(User user, String key) {
        TypeReference<HashMap<String, UserSimpleInfoDto>> typeReference = new TypeReference<>() {
        };
        Map<String, UserSimpleInfoDto> hashMap = getData(key, typeReference);
        return hashMap.containsKey(user.getUserId());
    }


    /**
     * map 형태로 된 cache Size 반환
     * @param key
     * @return
     */
    public Integer getRedisTotalCount(String key) {
        Integer count = null;
        TypeReference<HashMap<String, UserSimpleInfoDto>> typeReference = new TypeReference<>() {};
        HashMap<String, UserSimpleInfoDto> data = null;

        if (hasRedis(key)) {
            data = getData(key, typeReference);
            count = data.size();
        }
        return count;
    }
    /**
     * key(String)
     * value(hashMap)
     * 형태로 Redis 에 저장
     * upsert는 "update"와 "insert"를 결합한 용어
     *
     * @param o
     * @param key
     * @param subValueKey
     * @param <T>
     * @return
     */
    // redis 에 caching 데이터 찾은 후 존재하지 않을 경우 등록
    public <T> T upsertCacheMapValueByKey(T o, String key, String subValueKey) {
        Map<String, T> objectMap = null;
        String cachingData = null;
        try {
            // redis 에 존재하는 경우
            if (this.hasRedis(key)) { //
                TypeReference<HashMap<String, T>> typeReference = new TypeReference<>() {
                };
                objectMap = getData(key, typeReference);
                objectMap.put(subValueKey, o);
            } else {
                // redis 에 존재하지 않는 경우 새로 hashMap 생성
                objectMap = new HashMap<>();
                objectMap.put(subValueKey, o);
            }
            // hashMap -> jsonString 형태로 변환후
            // redis 에 저장
            cachingData = objectMapper.writeValueAsString(objectMap);
            setData(key, cachingData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return o;
    }

    /**
     * key(String)
     * value(hashMap)
     * subValueKey 에 해당하는 값 삭제
     *
     * @param o
     * @param key
     * @param subValueKey
     * @param <T>
     * @return
     */
    // redis 에 caching 데이터 찾은 후 존재하지 않을 경우 등록
    public <T> T removeCacheMapValueByKey(T o, String key, String subValueKey) {
        Map<String, T> objectMap = null;
        String cachingData = null;
        try {
            // redis 에 존재하는 경우
            if (this.hasRedis(key)) { //
                TypeReference<HashMap<String, T>> typeReference = new TypeReference<>() {
                };
                objectMap = getData(key, typeReference);
                objectMap.remove(subValueKey);
            }
            // hashMap -> jsonString 형태로 변환후
            // redis 에 저장
            cachingData = objectMapper.writeValueAsString(objectMap);
            setData(key, cachingData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return o;
    }

}

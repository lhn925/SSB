package sky.Sss.global.redis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sky.Sss.domain.user.dto.UserSimpleInfoDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.global.redis.dto.RedisKeyDto;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisCacheService {


    private final ObjectMapper objectMapper;
    private final RedisQueryService redisQueryService;

    public void setData(String key, Object value) {
        try {
            redisQueryService.setData(key, objectMapper.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public <T> T getData(String key, TypeReference<T> typeReference) {
        return redisQueryService.getData(key, typeReference);
    }

    public String getData(String key) {
        return redisQueryService.getData(key);
    }

    public Boolean delete(String key) {
        return redisQueryService.delete(key);
    }

    public Boolean hasRedis(String key) {
        return redisQueryService.hasRedis(key);
    }

//    /**
//     * track,ply,reply 의 like 횟수를 가져오는 Method
//     *
//     * @param key
//     * @param token
//     * @return
//     */
//    public Integer getLikeCount(String key, String token) {
//        TypeReference<HashMap<String, Integer>> typeReference = new TypeReference<>() {
//        };
//        HashMap<String, Integer> map;
//        Integer count = null;
//        if (hasRedis(key)) {
//            map = getData(key, typeReference);
//            count = map.get(token);
//        }
//        return count;
//    }

    /**
     * 유저의 FollowingTotal 을 가져오는 Method
     *
     */
//    public Integer getFollowingCount(String key, String subKey) {
//        return this.getLikeCount(key, subKey);
//    }
//
//    /**
//     * 유저의 FollowerTotal 을 가져오는 Method
//     *
//     */
//    public Integer getFollowerCount(String key, String subKey) {
//        return this.getLikeCount(key, subKey);
//    }

    /**
     * map 형태로 된 cache 에 값이 존재하는 지 boolean 값으로 반환 검색
     *
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
     * map 형태로 된 cache 에 값이 존재하는 지 boolean 값으로 반환 검색
     *
     * @param user
     * @param key
     * @return
     */
    public boolean existsByToken(User user, String key) {
        TypeReference<HashMap<String, UserSimpleInfoDto>> typeReference = new TypeReference<>() {
        };
        Map<String, UserSimpleInfoDto> hashMap = getData(key, typeReference);
        return hashMap.containsKey(user.getToken());
    }

    /**
     * map 형태로 된 cache Size 반환
     *
     * @param key
     * @return
     */
    public <C> int getTotalCountByKey(C collectionType, String key) {
        TypeReference<C> typeReference = new TypeReference<>() {
        };
        int count = 0;
        if (hasRedis(key)) {
            if (collectionType instanceof HashMap) {
                HashMap<Object, Object> map = (HashMap) getData(key, typeReference);
                count = map.size();
            } else if (collectionType instanceof HashSet) {
                HashSet<Object> set = (HashSet) getData(key, typeReference);
                count = set.size();
            }
        }
        return count;
    }

    /**
     * key(String)
     * value(hashMap)
     * 형태로 Redis 에 저장
     * upsert는 "update"와 "insert"를 결합한 용어
     *
     * @param value
     * @param key
     * @param subMapKey
     * @param <T>
     * @return
     */
    // redis 에 caching 데이터 찾은 후 존재하지 않을 경우 등록
    public <T> void upsertCacheMapValueByKey(T value, String key, String subMapKey) {
        Map<String, T> objectMap = null;

        String cachingData = null;
        // redis 에 존재하는 경우
        if (this.hasRedis(key)) { //
            TypeReference<HashMap<String, T>> typeReference = new TypeReference<>() {
            };
            objectMap = getData(key, typeReference);
            objectMap.put(subMapKey, value);

        } else {
            // redis 에 존재하지 않는 경우 새로 hashMap 생성
            objectMap = new HashMap<>();
            objectMap.put(subMapKey, value);
        }
        // hashMap -> jsonString 형태로 변환후
        // redis 에 저장
        setData(key, objectMap);

    }


    public void updateCacheMapValueByKey(String key, List<User> users) {
        Map<String, UserSimpleInfoDto> dataMap = users.stream()
            .collect(Collectors.toMap(User::getToken, UserSimpleInfoDto::new));
        setData(key, dataMap);
    }


    /**
     * key(String)
     * value(hashMap)
     * 형태로 Redis 에 저장
     * upsert는 "update"와 "insert"를 결합한 용어
     *
     * @param valueType
     * @param key
     * @param <T>
     */
    // redis 에 caching 데이터 찾은 후 존재하지 않을 경우 등록
    public <T> void upsertCacheSetValue(T valueType, String key) {
        HashSet<T> objectList = null;
        // redis 에 존재하는 경우
        if (this.hasRedis(key)) { //
            TypeReference<HashSet<T>> typeReference = new TypeReference<>() {
            };
            objectList = getData(key, typeReference);
            objectList.add(valueType);
        } else {
            // redis 에 존재하지 않는 경우 새로 hashMap 생성
            objectList = new HashSet<>();
            objectList.add(valueType);
        }
        // hashMap -> jsonString 형태로 변환후
        // redis 에 저장
        setData(key, objectList);
    }

    /**
     * key(String)
     * value(hashMap)
     * 형태로 Redis 에 저장
     * upsert는 "update"와 "insert"를 결합한 용어
     *
     * @param valueType
     * @param key
     * @param <T>
     */
    // redis 에 caching 데이터 찾은 후 존재하지 않을 경우 등록
    public <T> void removeCacheSetValue(T valueType, String key) {
        HashSet<T> objectList = null;
        // redis 에 존재하는 경우
        if (this.hasRedis(key)) { //
            TypeReference<HashSet<T>> typeReference = new TypeReference<>() {
            };
            objectList = getData(key, typeReference);
            objectList.remove(valueType);
        }
        // hashMap -> jsonString 형태로 변환후
        // redis 에 저장
        setData(key, objectList);


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
     */
    // redis 에 caching 데이터 찾은 후 존재하지 않을 경우 등록
    public <T> void removeCacheMapValueByKey(T o, String key, String subValueKey) {
        Map<String, T> objectMap = null;
        // redis 에 존재하는 경우
        if (hasRedis(key)) { //
            TypeReference<HashMap<String, T>> typeReference = new TypeReference<>() {
            };
            objectMap = getData(key, typeReference);
            objectMap.remove(subValueKey);
        }
        // hashMap -> jsonString 형태로 변환후
        // redis 에 저장

        // size 가 0 이면 자동으로 삭제
        if (objectMap.size() == 0) {
            log.info("접근 하겠습니다");
            Boolean delete = redisQueryService.delete(key);
            System.out.println("delete = " + delete);
        } else {
            setData(key, objectMap);
        }

    }

    public boolean hasWsStatusOnUser(String userToken) {
        /**
         * 만약 접속해 있지 않으면 Redis 에 저장 후
         * 로그인 시 전송
         */
        String wsTokenListKey = RedisKeyDto.REDIS_USER_WS_LIST_SESSION_KEY + userToken;

        Set<String> sessionSet = null;
        try {
            sessionSet = this.getData(wsTokenListKey, new TypeReference<>() {
            });
            // 존재하지 않으면 redis 삭제
            Set<String> filterSet = Optional.ofNullable(sessionSet).orElseGet(Collections::emptySet).stream()
                .filter(session -> {
                    boolean isResult = this.hasRedis(RedisKeyDto.REDIS_WS_SESSION_KEY + session);
                    if (!isResult) {
                        log.info("session = {}", session);
                        this.removeCacheSetValue(session, wsTokenListKey);
                    }
                    return isResult;
                }).collect(Collectors.toSet());

            return filterSet.isEmpty();
        } catch (IllegalArgumentException e) {
            return true;
        }
    }


}

package sky.Sss.global.redis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
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
import sky.Sss.global.redis.dto.RedisDataListDto;
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

    public <T> RedisDataListDto<T> getDataList(List<String> keys, TypeReference<T> typeReference, String redisKeyDto) {
        return redisQueryService.getDataList(keys, typeReference, redisKeyDto);
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
     * @param subKey
     *     : userId,userToken
     * @param key
     * @return
     */
    public boolean existsBySubKey(String subKey, String key) {
        TypeReference<HashMap<String, UserSimpleInfoDto>> typeReference = new TypeReference<>() {
        };
        Map<String, UserSimpleInfoDto> hashMap = getData(key, typeReference);
        if (hashMap == null) {
            return false;
        }
        return hashMap.containsKey(subKey);
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
            try {
                if (collectionType instanceof HashMap) {
                    HashMap map = (HashMap) getData(key, typeReference);
                    count = map.size();
                } else if (collectionType instanceof HashSet) {
                    HashSet set = (HashSet) getData(key, typeReference);
                    count = set.size();
                }
            } catch (NullPointerException e) {
                return 0;
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
        Map<String, T> objectMap = new HashMap<>();
        // redis 에 존재하는 경우
        if (this.hasRedis(key)) { //
            TypeReference<HashMap<String, T>> typeReference = new TypeReference<>() {
            };
            objectMap = getData(key, typeReference);
            if (objectMap == null) {
                objectMap = new HashMap<>();
            }
            objectMap.put(subMapKey, value);
        } else {
            objectMap.put(subMapKey, value);
        }
        // hashMap -> jsonString 형태로 변환후
        // redis 에 저장
        setData(key, objectMap);

    }

    /**
     * key(String)
     * value(hashMap)
     * 형태로 Redis 에 저장
     * upsert는 "update"와 "insert"를 결합한 용어
     *
     * @param addMap
     * @param key
     * @param <T>
     */
    // redis 에 caching 데이터 찾은 후 존재하지 않을 경우 등록
    public <T> void upsertAllCacheMapValuesByKey(Map<String, T> addMap, String key) {
        Map<String, T> objectMap;
        // redis 에 존재하는 경우
        if (this.hasRedis(key)) { //
            TypeReference<Map<String, T>> typeReference = new TypeReference<>() {
            };
            objectMap = getData(key, typeReference);
            if (objectMap == null) {
                objectMap = new HashMap<>();
            }
            objectMap.putAll(addMap);
        } else {
            objectMap = new HashMap<>(addMap);
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
     * value(HashSet)
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
        if (objectMap == null || objectMap.isEmpty()) {
            redisQueryService.delete(key);
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
                        this.removeCacheSetValue(session, wsTokenListKey);
                    }
                    return isResult;
                }).collect(Collectors.toSet());

            return filterSet.isEmpty();
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    public <T> T getCacheMapValueBySubKey(Class<T> clazz, String subKey, String redisMapKey) {
        Map<String, T> redisTrackMap = getRedisTrackMap(redisMapKey);
        if (redisTrackMap == null || !redisTrackMap.containsKey(subKey)) {
            return null;
        }
        Object subKeyData = redisTrackMap.get(subKey);
        return objectMapper.convertValue(subKeyData, clazz);
    }



    // 레디스에서 데이터를 가져와서 카운트를 설정하는 기능
    public <T> RedisDataListDto<Map<String, T>> fetchAndCountFromRedis(
        List<String> tokens,
        String redisKeyDto,

        Map<String, Integer> countMap
    ) {
        int count;
        TypeReference<Map<String, T>> typeReference = new TypeReference<>() {
        };

        RedisDataListDto<Map<String, T>> dataList = getDataList(tokens, typeReference, redisKeyDto);

        // 레디스에 있는 좋아요 수 countMap 에 put
        for (String targetToken : tokens) {
            count = 0;
            Map<String, T> simpleInfoDtoHashMap = dataList.getResult().get(targetToken);
            if (simpleInfoDtoHashMap != null) {
                count = simpleInfoDtoHashMap.size();
            }
            countMap.put(targetToken, count);
        }
        return dataList;
    }


    public <T> RedisDataListDto<T> getCacheMapValuesBySubKey(Class<T> clazz, Set<String> subKeyList,
        String redisMapKey) {

        Map<String, T> redisTrackMap = getRedisTrackMap(redisMapKey);

        Map<String, T> result = new HashMap<>();
        if (redisTrackMap == null) {
            return new RedisDataListDto<>(result, new HashSet<>(subKeyList));
        }

        Set<String> missingKeys = new HashSet<>();
        for (String subKey : subKeyList) {
            Object subKeyData = redisTrackMap.get(subKey);
            if (subKeyData == null) {
                missingKeys.add(subKey);
            } else {
                result.put(subKey, objectMapper.convertValue(subKeyData, clazz));
            }
        }
        return new RedisDataListDto<>(result, missingKeys);
    }

    private <T> Map<String, T> getRedisTrackMap(String redisMapKey) {
        TypeReference<Map<String, T>> redisType = new TypeReference<>() {
        };

        Map<String, T> redisTrackMap;
        try {
            redisTrackMap = getData(redisMapKey, redisType);
        } catch (Exception e) {
            // 예외 처리 로직
            e.printStackTrace();
            return null;
        }
        return redisTrackMap;
    }


}

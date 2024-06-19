package sky.Sss.global.redis.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheManagerService {

    private final CacheManager cacheManager;

    public <T> void addCachingData(Map<String, T> cachingDataMap, String redisKey) {
        for (Map.Entry<String, T> entry : cachingDataMap.entrySet()) {
            T cachingData = entry.getValue();
            if (cachingData != null) {
                Objects.requireNonNull(cacheManager.getCache(redisKey))
                    .put(entry.getKey(), cachingData);
            }
        }
    }
    public <T> Map<String, T> getCachingData(Set<String> keys, String redisKey, Class<T> clazz) {
        Cache cache = cacheManager.getCache(redisKey);
        if (cache == null) {
            return Collections.emptyMap();
        }
        Map<String, T> dataMap = new HashMap<>();
        for (String key : keys) {
            T data = cache.get(key, clazz);
            if (data != null) {
                dataMap.put(key, data);
            }
        }
        return dataMap;
    }
}

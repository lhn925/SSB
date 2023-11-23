package sky.Sss.config;


import java.time.Duration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    //사용할 의존 객체를 선택할 수 있도록 해준다
//    @Autowired
//    어노테이션이 적용된 주입 대상에 @Qualifier 어노테이션을 설정한다.
//    이때 @Qualifier의 값으로 앞서 설정한 한정자를 사용한다.

    @Bean
    public CacheManager contentCacheManager(@Qualifier("redisConnectionFactory") RedisConnectionFactory cf) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(
                SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
            .entryTtl(Duration.ofMinutes(3L));// 캐시수명 30분

        return RedisCacheManagerBuilder.fromConnectionFactory(cf)
            .cacheDefaults(redisCacheConfiguration).build();
    }

}

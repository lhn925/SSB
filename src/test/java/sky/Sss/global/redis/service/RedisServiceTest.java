package sky.Sss.global.redis.service;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;


@SpringBootTest
class RedisServiceTest {

    @Autowired
    RedisService redisService;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    HashOperations<String, String, Object> hashOperations;


    @Test
    public void getTest() throws IOException, ClassNotFoundException {

        String redisStr = (String) redisTemplate.opsForHash()
            .get("spring:session:sessions:6b1c8ff9-01fa-4f99-9e8b-468eea6cf7dc", "sessionAttr:USER_ID");
        hashOperations = redisTemplate.opsForHash();


    }

}
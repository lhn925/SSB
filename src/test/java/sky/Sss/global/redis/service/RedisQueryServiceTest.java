package sky.Sss.global.redis.service;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import sky.Sss.domain.user.dto.UserSimpleInfoDto;
import sky.Sss.global.redis.dto.RedisDataListDto;
import sky.Sss.global.redis.dto.RedisKeyDto;


@SpringBootTest
class RedisQueryServiceTest {

    @Autowired
    RedisQueryService redisQueryService;

    @Autowired
    RedisTemplate redisTemplate;


    @Test
    public void getTestList() throws IOException, ClassNotFoundException {

        TypeReference<Map<String, UserSimpleInfoDto>> typeReference = new TypeReference<>() {
        };

        List<String> keys = new ArrayList<>();

        keys.add("0aac203d6b51f3840d40");
        keys.add("22d182fe5d2d9b09667b");
        keys.add("ebb90c922de3f3082dc8");

        RedisDataListDto<Map<String, UserSimpleInfoDto>> dataList = redisQueryService.getDataList(keys,
            typeReference, RedisKeyDto.REDIS_TRACK_LIKES_MAP_KEY);

        System.out.println("dataList = " + dataList.getResult());
        System.out.println("dataList.getMissingKeys() = " + dataList.getMissingKeys());

        for (String s : dataList.getResult().keySet()) {
            Map<String, UserSimpleInfoDto> stringUserSimpleInfoDtoMap = dataList.getResult().get(s);
        }

    }


}
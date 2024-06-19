package sky.Sss.domain.user.service.follows;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.Sss.domain.user.dto.redis.RedisFollowsDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.UserFollows;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.global.redis.dto.RedisDataListDto;
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisCacheService;

@SpringBootTest
class UserFollowsServiceTest {

    @Autowired
    UserFollowsService userFollowsService;

    @Autowired
    UserQueryService userQueryService;

    @Autowired
    RedisCacheService redisCacheService;
    @Test
    public void getFollowingUsersByTokens() {

        User user1 = userQueryService.findOne("lim222");
        User user2 = userQueryService.findOne("lim2226");

        List<String> tokens = new ArrayList<>();

        tokens.add(user1.getToken());
        tokens.add(user2.getToken());

        Map<String, List<RedisFollowsDto>> followingUsers = userFollowsService.getFollowingUsersByTokens(tokens);

        for (String s : followingUsers.keySet()) {
        }



    }


    @Test
    public void getFollowerUsersByTokens() {

        User user1 = userQueryService.findOne("lim222");
        User user2 = userQueryService.findOne("lim2226");
        User user3 = userQueryService.findOne("0221325");

        List<String> tokens = new ArrayList<>();

        tokens.add(user1.getToken());
        tokens.add(user2.getToken());
        tokens.add(user3.getToken());

        Map<String, List<RedisFollowsDto>> followingUsers = userFollowsService.getFollowerUsersByTokens(tokens);

        for (String s : followingUsers.keySet()) {
            List<RedisFollowsDto> userFollowsList = followingUsers.get(s);

            for (RedisFollowsDto userFollows : userFollowsList) {


            }

        }
    }


    @Test
    public void getFollowingMapFromCacheOrDB() {

        User user1 = userQueryService.findOne("lim222");
        User user2 = userQueryService.findOne("lim2226");
        User user3 = userQueryService.findOne("0221325");

        List<String> tokens = new ArrayList<>();

        tokens.add(user1.getToken());
        tokens.add(user2.getToken());
        tokens.add(user3.getToken());

        Map<String, List<RedisFollowsDto>> followingMapFromCacheOrDB = userFollowsService.getFollowMapFromCacheOrDBByType(
            tokens,RedisKeyDto.REDIS_USER_FOLLOWER_MAP_KEY);

        for (String s : followingMapFromCacheOrDB.keySet()) {
            System.out.println("token " + s);
            System.out.println("followingMapFromCacheOrDB.get(s).size() = " + followingMapFromCacheOrDB.get(s).size());
        }
    }
        

        


}
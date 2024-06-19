package sky.Sss.domain.user.service;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.Sss.domain.user.dto.redis.RedisUserDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Enabled;
import sky.Sss.domain.user.repository.UserQueryRepository;
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisCacheService;

@SpringBootTest
class UserQueryServiceTest {

    @Autowired
    RedisCacheService redisCacheService;

    @Autowired
    UserQueryService userQueryService;


    @Autowired
    UserQueryRepository userQueryRepository;
    @Test
    void findOne() {

        // 유저아이디와 유저 pk 값으로 이루어진 map 반환


        String userId = "lim2226";

        getUserInfoFromCacheOrDB(userId,RedisKeyDto.REDIS_USER_IDS_MAP_KEY);
        User userInfoFromCacheOrDB = getUserInfoFromCacheOrDB(userId, RedisKeyDto.REDIS_USER_EMAILS_MAP_KEY);

    }


    @Test
    public void RedisTest() {



        Set<Long> ids = new HashSet<>();

        ids.add(1L);
        ids.add(2L);
        ids.add(5437L);
        List<User> byIds = userQueryService.findUsersByIds(ids, Enabled.ENABLED);

        System.out.println("byIds.size() = " + byIds.size());

    }


    @Test
    public void UserNameTest() {
        Set<String> userNames = new HashSet<>();
        userNames.add("_sky_");
        userNames.add("임하늘");
        Set<User> usersByUserNames = userQueryService.findUsersByUserNames(userNames, Enabled.ENABLED);

        System.out.println("usersByUserNames = " + usersByUserNames);

    }

    private User getUserInfoFromCacheOrDB(String subKey , String redisUidMapKey) {
        TypeReference<HashMap<String, String>> type = new TypeReference<>() {
        };

        HashMap<String, String> userIdMap = redisCacheService.getData(redisUidMapKey, type);
        // map 널인 경우
        // 혹은 Map 에 없는 경우
        if (userIdMap == null || !userIdMap.containsKey(subKey)) {
            return fetchAndSetUserInReds(subKey);
        }

        String uid = String.valueOf(userIdMap.get(subKey));

        TypeReference<HashMap<String, RedisUserDto>> redisDtoType = new TypeReference<>() {};

        String redisUsersInfoMapKey = RedisKeyDto.REDIS_USERS_INFO_MAP_KEY;

        Map<String, RedisUserDto> userInfoMap = redisCacheService.getData(redisUsersInfoMapKey, redisDtoType);
        if (userInfoMap == null || !userInfoMap.containsKey(uid)) {
            return fetchAndSetUserInReds(subKey);
        }
        RedisUserDto redisUserDTO = userInfoMap.get(uid);

        return User.redisUserDtoToUser(redisUserDTO);
    }

    private User fetchAndSetUserInReds(String userId) {
        User entityUser = userQueryService.findOne(userId);
        RedisUserDto redisUserDTO = RedisUserDto.create(entityUser);
        setUserInfoDtoRedis(redisUserDTO);
        return entityUser;
    }


    @Test
    public void findUsersByIds() {

    // given

    // when

    // then

    }

    private void setUserInfoDtoRedis(RedisUserDto redisUserDTO) {
        redisCacheService.upsertCacheMapValueByKey(redisUserDTO.getId(), RedisKeyDto.REDIS_USER_IDS_MAP_KEY,
            redisUserDTO.getUserId());
        redisCacheService.upsertCacheMapValueByKey(redisUserDTO.getId(),  RedisKeyDto.REDIS_USER_NAMES_MAP_KEY,
            redisUserDTO.getUserName());
        redisCacheService.upsertCacheMapValueByKey(redisUserDTO.getId(), RedisKeyDto.REDIS_USER_EMAILS_MAP_KEY,
            redisUserDTO.getEmail());
        redisCacheService.upsertCacheMapValueByKey(redisUserDTO, RedisKeyDto.REDIS_USERS_INFO_MAP_KEY,
            String.valueOf(redisUserDTO.getId()));
    }
}
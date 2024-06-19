package sky.Sss.domain.user.service.follows;


import com.fasterxml.jackson.core.type.TypeReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.user.dto.redis.RedisFollowsDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.UserFollows;
import sky.Sss.domain.user.model.Enabled;
import sky.Sss.domain.user.repository.follow.UserFollowsRepository;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.global.redis.dto.RedisDataListDto;
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisCacheService;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserFollowsService {

    private final UserFollowsRepository userFollowRepository;
    private final RedisCacheService redisCacheService;
    private final UserQueryService userQueryService;


    @Transactional
    public void addUserFollows(UserFollows userFollows) {
        userFollowRepository.save(userFollows);

        // 요청 유저의 팔로윙 list update
        addRedisUserFollowingList(userFollows);

        // 대상 유저 의 팔로우 list update
        addRedisUserFollowerList(userFollows);
    }

    // 유저의 Following List update
    private void addRedisUserFollowingList(UserFollows userFollows) {
        // 유저가 팔로우를 하고 있는 목록 키 값 반환
        String userFollowingListKey = getUserFollowingListKey(userFollows.getFollowerUser().getToken());

        //  Map 에서 유저 구분 subKey 값
        String subFollowingUserKey = userFollows.getFollowingUser().getToken();

        // followerUser 의 followingList 업데이트
        redisCacheService.upsertCacheMapValueByKey(RedisFollowsDto.create(userFollows), userFollowingListKey,
            subFollowingUserKey);
    }

    // 유저의 Follower List update
    private void addRedisUserFollowerList(UserFollows userFollows) {
        // FollowingUser 를 팔로우 하고 있는 목록 키 값 반환
        String userFollowerListKey = getUserFollowerListKey(userFollows.getFollowingUser().getToken());

        // followingUser 의 followerList 업데이트
        String subFollowerUserKey = userFollows.getFollowerUser().getToken();

        // followingUser 의 followerList 업데이트
        redisCacheService.upsertCacheMapValueByKey(RedisFollowsDto.create(userFollows), userFollowerListKey,
            subFollowerUserKey);
    }


    /**
     * @param followerUser
     *     팔로우 요청자
     * @param followingUser
     *     팔로우 대상
     * @return
     */
    public boolean existsFollowing(User followerUser, User followingUser) {

        boolean isExists = false;
        // 팔로우 요청자의 redis Cache 를 검색 후 있는지 확인
        String key = getUserFollowingListKey(followerUser.getToken());
        // redis 에 있는지 확인
        if (redisCacheService.hasRedis(key)) {
            isExists = redisCacheService.getCacheMapValueBySubKey(RedisFollowsDto.class,
                followingUser.getToken(), key) != null;
        }

        if (!isExists) {
            UserFollows userFollows = findFollowingByFollowerUser(
                followerUser, followingUser);
            isExists = userFollows != null;
            // 만약 레디스에는 없고 디비에는 있으면
            if (isExists) {
                // 요청자의 following list 업데이트
                addRedisUserFollowingList(userFollows);
                // followingUser 의 follower List 업데이트
                addRedisUserFollowerList(userFollows);
            }
        }
        return isExists;
    }
    //delete


    @Transactional
    public void cancelFollow(UserFollows userFollows) {
        // 엔티티 객체 삭제
        delete(userFollows);

        String followerUserToken = userFollows.getFollowerUser().getToken();
        String followingUserToken = userFollows.getFollowingUser().getToken();

        String userFollowingListKey = getUserFollowingListKey(followerUserToken);
        String userFollowerListKey = getUserFollowerListKey(followingUserToken);

        // 요청 사용자의 following update
        redisCacheService.removeCacheMapValueByKey(new RedisFollowsDto(), userFollowingListKey, followingUserToken);

        // 팔로우 취소 대상자의 Follower update
        redisCacheService.removeCacheMapValueByKey(new RedisFollowsDto(), userFollowerListKey, followerUserToken);
    }

    @Transactional
    public void delete(UserFollows userFollows) {
        userFollowRepository.delete(userFollows);
    }

    // 팔로우값 쿼리
    public UserFollows findFollowingByFollowerUser(User followerUser, User followingUser) {
        return userFollowRepository.findByFollowingUserAndFollowerUser(followerUser, followingUser).orElse(null);
    }

    // likes Total 레디스에서 검색 후 존재하지 않으면 DB 검색 후 반환 검색

    // 유저를 팔로우 하고 있는 count
    public int getFollowerTotalCount(User user) {
        String key = RedisKeyDto.REDIS_USER_FOLLOWER_MAP_KEY + user.getToken();

        int count = 0;
        // redis 에 total 캐시가 있으면
        count = redisCacheService.getTotalCountByKey(new HashMap<>(), key);
        if (count == 0) {
            List<RedisFollowsDto> myFollowerUsers = getMyFollowerUsers(user);
            count = myFollowerUsers.size();
        }
        return count;
    }


    public List<RedisFollowsDto> getFollowersUsersFromCacheOrDB(User user) {
        String key = getUserFollowerListKey(user.getToken());
        TypeReference<Map<String, RedisFollowsDto>> typeReference = new TypeReference<>() {
        };
        Map<String, RedisFollowsDto> followersMap = redisCacheService.getData(key, typeReference);
        if (followersMap == null || followersMap.isEmpty()) {
            return getMyFollowerUsers(user);
        }
        enabledUserFilter(key, followersMap);
        return followersMap.values().stream().toList();
    }

    public List<RedisFollowsDto> getFollowingUsersFromCacheOrDB(User user) {
        String key = getUserFollowingListKey(user.getToken());
        TypeReference<Map<String, RedisFollowsDto>> typeReference = new TypeReference<>() {
        };

        // redis에서 가져온 followingMap
        Map<String, RedisFollowsDto> followingMap = redisCacheService.getData(key, typeReference);
        if (followingMap == null || followingMap.isEmpty()) {
            return getMyFollowingUsers(user);
        }

        enabledUserFilter(key, followingMap);
        return followingMap.values().stream().toList();
    }

    /**
     * DB에 검색 되지 않는 User 삭제
     *
     * @param key
     * @param followMap
     */
    private void enabledUserFilter(String key, Map<String, RedisFollowsDto> followMap) {
        Map<String, User> followingUserMap = userQueryService.getUserListByTokens(followMap.keySet(),
                Enabled.ENABLED)
            .stream().collect(
                Collectors.toMap(User::getToken, Function.identity()));

        List<String> removeKey = new ArrayList<>();
        for (String followings : followMap.keySet()) {
            // DB에 검색되지않는  User redis 에서 삭제
            if (!followingUserMap.containsKey(followings)) {
                followMap.remove(followings);
                removeKey.add(followings);
            }
        }
        if (!removeKey.isEmpty()) {
            redisCacheService.removeCacheMapValuesByKey(new RedisFollowsDto(), key, removeKey);
        }
    }
    // 유저들이 팔로우 하고 있는 map
    // 유저토큰,FollowerList
    public Map<String, List<RedisFollowsDto>> getFollowMapFromCacheOrDBByType(List<String> tokens, String redisKeyDto) {

        Map<String, List<RedisFollowsDto>> userFollowsMap = new HashMap<>();
        RedisDataListDto<Map<String, RedisFollowsDto>> mapRedisData = redisCacheService.fetchAndCountFromRedis(tokens,
            redisKeyDto, null);

        // 아예 없을 경우
        if (mapRedisData.getResult().isEmpty()) {
            return getStringListMapByType(tokens, redisKeyDto);

        }
        Map<String, Map<String, RedisFollowsDto>> result = mapRedisData.getResult();

        for (String userToken : result.keySet()) {

            Map<String, RedisFollowsDto> redisFollowsDtoMap = result.get(userToken);

            // 검색되지 않은 회원 삭제
            enabledUserFilter(redisKeyDto + userToken, redisFollowsDtoMap);

            userFollowsMap.put(userToken, redisFollowsDtoMap.values().stream().toList());
        }

        // 찾고자하는 Key 가 없을 경우
        if (mapRedisData.getMissingKeys().isEmpty()) {
            return userFollowsMap;
        }

        Set<String> missingTokens = mapRedisData.getMissingKeys();

        Map<String, List<RedisFollowsDto>> followUsersByTokens = getStringListMapByType(new ArrayList<>(missingTokens),
            redisKeyDto);


        userFollowsMap.putAll(followUsersByTokens);

        return userFollowsMap;
    }

    private Map<String, List<RedisFollowsDto>> getStringListMapByType(List<String> tokens, String redisKeyDto) {
        if (redisKeyDto.equals(RedisKeyDto.REDIS_USER_FOLLOWING_MAP_KEY)) {
            return getFollowingUsersByTokens(tokens);
        } else {
            return getFollowerUsersByTokens(tokens);
        }
    }


    public List<User> getFollowerListFromCacheOrDB(User user) {
        String key = getUserFollowerListKey(user.getToken());
        TypeReference<Map<String, RedisFollowsDto>> typeReference = new TypeReference<>() {
        };

        Set<String> tokens = redisCacheService.getData(key, typeReference).keySet();

        return userQueryService.getUserListByTokens(tokens, Enabled.ENABLED);
    }


    // 유저가 팔로우 하고 있는 following count
    public int getFollowingTotalCount(User user) {
        String key = RedisKeyDto.REDIS_USER_FOLLOWING_MAP_KEY + user.getToken();
        // redis 에 total 캐시가 있으면
        int count = 0;
        count = redisCacheService.getTotalCountByKey(new HashMap<>(), key);
        // redis 에 저장이 안되어 있을경우 count 후 저장
        if (count == 0) {
            List<RedisFollowsDto> myFollowingUsers = getMyFollowingUsers(user);
            count = myFollowingUsers.size();
        }
        return count;
    }


    // 유저를 팔로우 하고 있는 총 유저 수 총합
    public List<RedisFollowsDto> getMyFollowerUsers(User user) {
        List<UserFollows> myFollowerUsers = userFollowRepository.getMyFollowerUsers(user, Enabled.ENABLED());
        Map<String, RedisFollowsDto> redisFollowsDtoMap = myFollowerUsers.stream().collect(
            Collectors.toMap(mapKey -> mapKey.getFollowerUser().getToken(),
                RedisFollowsDto::create));
        redisCacheService.upsertAllCacheMapValuesByKey(redisFollowsDtoMap, getUserFollowerListKey(user.getToken()));
        return redisFollowsDtoMap.values().stream().toList();
    }

    // 유저가 총 팔로우 하고 있는 유저 수 총합
    public List<RedisFollowsDto> getMyFollowingUsers(User user) {
        List<UserFollows> myFollowingUsers = userFollowRepository.myFollowingUsers(user, Enabled.ENABLED());
        Map<String, RedisFollowsDto> map = myFollowingUsers.stream().collect(
            Collectors.toMap(mapKey -> mapKey.getFollowingUser().getToken(),
                RedisFollowsDto::create));
        redisCacheService.upsertAllCacheMapValuesByKey(map, getUserFollowingListKey(user.getToken()));
        return map.values().stream().toList();
    }

    // 유저들이 팔로우 하고 있는 유저 검색
    public Map<String, List<RedisFollowsDto>> getFollowingUsersByTokens(List<String> tokens) {
        List<UserFollows> followingList = userFollowRepository.followingUsersByTokens(tokens, Enabled.ENABLED());
        // Step 1: Create userFollowsMap
        return setRedisFollowingMap(followingList);
    }

    // 유저들을 팔로우 하고 있는 유저 검색
    public Map<String, List<RedisFollowsDto>> getFollowerUsersByTokens(List<String> tokens) {
        List<UserFollows> followerList = userFollowRepository.followerUsersByTokens(tokens, Enabled.ENABLED());
        return setRedisFollowerMap(followerList);
    }


    private Map<String, List<RedisFollowsDto>> setRedisFollowingMap(List<UserFollows> followingList) {
        Map<String, List<UserFollows>> userFollowsMap = followingList.stream()
            .collect(Collectors.groupingBy(key1 -> key1.getFollowerUser().getToken(),
                Collectors.mapping(val -> val, Collectors.toList())));
        // Step 2: Create userMap using userFollowsMap
        Map<String, Map<String, RedisFollowsDto>> userMap = userFollowsMap.entrySet().stream()
            .collect(Collectors.toMap(
                Entry::getKey, // key1: FollowerUser token
                entry -> entry.getValue().stream()
                    .collect(Collectors.toMap(
                        uf -> uf.getFollowingUser().getToken(), // key2: FollowingUser token
                        RedisFollowsDto::create
                    ))
            ));
        // 결과 사용
        Map<String, List<RedisFollowsDto>> keyValList = new HashMap<>();
        for (Entry<String, Map<String, RedisFollowsDto>> entry : userMap.entrySet()) {
            Map<String, RedisFollowsDto> cachingData = entry.getValue();
            keyValList.put(entry.getKey(), cachingData.values().stream().toList());
            redisCacheService.upsertAllCacheMapValuesByKey(cachingData, getUserFollowingListKey(entry.getKey()));
        }
        return keyValList;
    }

    private Map<String, List<RedisFollowsDto>> setRedisFollowerMap(List<UserFollows> followerList) {
        // Step 1: Create userFollowsMap
        Map<String, List<UserFollows>> userFollowsMap = followerList.stream()
            .collect(Collectors.groupingBy(key1 -> key1.getFollowingUser().getToken(),
                Collectors.mapping(val -> val, Collectors.toList())));
        // Step 2: Create userMap using userFollowsMap
        Map<String, Map<String, RedisFollowsDto>> userMap = userFollowsMap.entrySet().stream()
            .collect(Collectors.toMap(
                Entry::getKey, // key1: FollowerUser token
                entry -> entry.getValue().stream()
                    .collect(Collectors.toMap(
                        uf -> uf.getFollowerUser().getToken(), // key2: FollowingUser token
                        RedisFollowsDto::create
                    ))
            ));
        Map<String, List<RedisFollowsDto>> keyValList = new HashMap<>();
        // 결과 사용
        for (Entry<String, Map<String, RedisFollowsDto>> entry : userMap.entrySet()) {
            Map<String, RedisFollowsDto> cachingData = entry.getValue();
            keyValList.put(entry.getKey(), cachingData.values().stream().toList());
            redisCacheService.upsertAllCacheMapValuesByKey(cachingData, getUserFollowerListKey(entry.getKey()));
        }
        return keyValList;
    }


    // 유저를 팔로우 하고 있는 총 유저 수 총합
    public Integer getFollowerCountByUser(User followingUser) {
        return userFollowRepository.countByFollowingUser(followingUser);
    }

    // 유저가 총 팔로우 하고 있는 유저 수 총합
    public Integer getFollowingCountByUser(User followerUser) {
        return userFollowRepository.countByFollowerUser(followerUser);
    }

    // 유저가 팔로우 하고 있는 사람들의 목록 키 값
    public String getUserFollowingListKey(String userToken) {
        return RedisKeyDto.REDIS_USER_FOLLOWING_MAP_KEY + userToken;
    }

    // 유저를 팔로우 하고 있는 사람들의 목록 키 값
    public String getUserFollowerListKey(String userToken) {
        return RedisKeyDto.REDIS_USER_FOLLOWER_MAP_KEY + userToken;
    }


}

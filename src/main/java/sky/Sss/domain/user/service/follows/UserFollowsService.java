package sky.Sss.domain.user.service.follows;


import com.fasterxml.jackson.core.type.TypeReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            List<UserFollows> myFollowerUsers = getMyFollowerUsers(user);
            count = myFollowerUsers.size();
        }
        return count;
    }


    public List<UserFollows> getFollowersUsersFromCacheOrDB(User user) {
        String key = getUserFollowerListKey(user.getToken());
        TypeReference<Map<String, RedisFollowsDto>> typeReference = new TypeReference<>() {
        };
        Map<String, RedisFollowsDto> followersMap = redisCacheService.getData(key, typeReference);
        if (followersMap == null || followersMap.isEmpty()) {
            return getMyFollowerUsers(user);
        }
        Map<String, User> followerUserMap = userQueryService.getUserListByTokens(followersMap.keySet(), Enabled.ENABLED)
            .stream().collect(
                Collectors.toMap(User::getToken, Function.identity()));
        List<UserFollows> users = new ArrayList<>();
        for (String follower : followersMap.keySet()) {
            RedisFollowsDto redisFollowsDto = followersMap.get(follower);
            // 팔로윙 유저 list에 추가
            if (followerUserMap.containsKey(follower)) {
                users.add(UserFollows.redisFollowsToUserFollow(redisFollowsDto, followerUserMap.get(follower), user));
            } else { // DB에 검색되지않는  User redis 에서 삭제
                redisCacheService.removeCacheMapValueByKey(new RedisFollowsDto(), key, follower);
            }
        }
        return users;
    }

    public List<UserFollows> getFollowingUsersFromCacheOrDB(User user) {
        String key = getUserFollowingListKey(user.getToken());
        TypeReference<Map<String, RedisFollowsDto>> typeReference = new TypeReference<>() {
        };
        Map<String, RedisFollowsDto> followingMap = redisCacheService.getData(key, typeReference);
        if (followingMap == null || followingMap.isEmpty()) {
            return getMyFollowingUsers(user);
        }

        Map<String, User> followingUserMap = userQueryService.getUserListByTokens(followingMap.keySet(),
                Enabled.ENABLED)
            .stream().collect(
                Collectors.toMap(User::getToken, Function.identity()));

        List<UserFollows> users = new ArrayList<>();
        for (String followings : followingMap.keySet()) {
            RedisFollowsDto redisFollowsDto = followingMap.get(followings);
            // 팔로윙 유저 list에 추가
            if (followingUserMap.containsKey(followings)) {
                users.add(
                    UserFollows.redisFollowsToUserFollow(redisFollowsDto, user, followingUserMap.get(followings)));
            } else { // DB에 검색되지않는  User redis 에서 삭제
                redisCacheService.removeCacheMapValueByKey(new RedisFollowsDto(), key, followings);
            }
        }
        return users;
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
            List<UserFollows> myFollowingUsers = getMyFollowingUsers(user);
            count = myFollowingUsers.size();
        }
        return count;
    }


    // 유저를 팔로우 하고 있는 총 유저 수 총합
    public List<UserFollows> getMyFollowerUsers(User user) {
        List<UserFollows> myFollowerUsers = userFollowRepository.getMyFollowerUsers(user);
        Map<String, RedisFollowsDto> map = myFollowerUsers.stream().collect(
            Collectors.toMap(mapKey -> mapKey.getFollowerUser().getToken(),
                RedisFollowsDto::create));
        redisCacheService.upsertAllCacheMapValuesByKey(map, getUserFollowerListKey(user.getToken()));
        return myFollowerUsers;
    }

    // 유저가 총 팔로우 하고 있는 유저 수 총합
    public List<UserFollows> getMyFollowingUsers(User user) {
        List<UserFollows> myFollowingUsers = userFollowRepository.myFollowingUsers(user);
        Map<String, RedisFollowsDto> map = myFollowingUsers.stream().collect(
            Collectors.toMap(mapKey -> mapKey.getFollowingUser().getToken(),
                RedisFollowsDto::create));
        redisCacheService.upsertAllCacheMapValuesByKey(map, getUserFollowingListKey(user.getToken()));
        return myFollowingUsers;
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

package sky.Sss.domain.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.user.dto.UserSimpleInfoDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.UserFollows;
import sky.Sss.domain.user.repository.UserFollowsRepository;
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisCacheService;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserFollowsService {

    private final UserFollowsRepository userFollowRepository;
    private final RedisCacheService redisCacheService;

    @Transactional
    public void addUserFollows(UserFollows userFollows) {
        userFollowRepository.save(userFollows);


        // 요청 유저의 팔로윙 list update
        addRedisUserFollowingList(userFollows.getFollowerUser(),userFollows.getFollowingUser());

        // 대상 유저 의 팔로우 list update
        addRedisUserFollowerList(userFollows.getFollowingUser(),userFollows.getFollowerUser());
    }
    // 유저의 Following List update
    private void addRedisUserFollowingList(User followerUser,User followingUser) {
        // 유저가 팔로우를 하고 있는 목록 키 값 반환
        String userFollowingListKey = getUserFollowingListKey(followerUser.getToken());

        //  Map 에서 유저 구분 subKey 값
        String subFollowingUserKey = followingUser.getToken();

        // followerUser 의 followingList 업데이트
        redisCacheService.upsertCacheMapValueByKey(new UserSimpleInfoDto(followingUser), userFollowingListKey,
            subFollowingUserKey);
    }
    // 유저의 Follower List update
    private void addRedisUserFollowerList(User followingUser,User followerUser) {
        // FollowingUser 를 팔로우 하고 있는 목록 키 값 반환
        String userFollowerListKey = getUserFollowerListKey(followingUser.getToken());

        // followingUser 의 followerList 업데이트
        String subFollowerUserKey = followerUser.getToken();

        // followingUser 의 followerList 업데이트
        redisCacheService.upsertCacheMapValueByKey(new UserSimpleInfoDto(followerUser), userFollowerListKey,
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

        // 팔로우 요청자의 redis Cache 를 검색 후 있는지 확인
        String key = getUserFollowingListKey(followerUser.getToken());
        // redis 에 있는지 확인
        if (redisCacheService.hasRedis(key)) {
            return redisCacheService.existsByToken(followingUser, key);
        }
        UserFollows userFollows = findFollowingByFollowerUser(
            followerUser, followingUser);

        boolean isExist = userFollows != null;

        // 만약 레디스에는 없고 디비에는 있으면
        if (isExist) {
            // 요청자의 following list 업데이트
            addRedisUserFollowingList(followerUser,followingUser);
            // followingUser 의 follower List 업데이트
            addRedisUserFollowerList(followingUser,followerUser);
        }
        return isExist;
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
        redisCacheService.removeCacheMapValueByKey(new UserSimpleInfoDto(),userFollowingListKey,followingUserToken);

        // 팔로우 취소 대상자의 Follower update
        redisCacheService.removeCacheMapValueByKey(new UserSimpleInfoDto(),userFollowerListKey,followerUserToken);
    }

    @Transactional
    public void delete(UserFollows userFollows) {
        userFollowRepository.delete(userFollows);
    }

    // 팔로우값 쿼리
    public UserFollows findFollowingByFollowerUser (User followerUser,User followingUser) {
        return userFollowRepository.findByFollowingUserAndFollowerUser(followerUser, followingUser).orElse(null);
    }

    // 유저의 Following Total 업데이트
    // 유저의 follower Total 업데이트
    public void updateTotalCount(String redisKey,String totalKey, User user) {
        // likes Size 를 구하긴 위한 key 값

        String userToken = user.getToken();
        String key = redisKey + userToken;

        // redis 에서 총 size 검색
        Integer count = redisCacheService.getRedisTotalCount(key);

        count = count != null ? count :
            // redis 의 키가 followingList key와 같으면 followingCount 를 아니면 FollowerCount
            redisKey.equals(RedisKeyDto.REDIS_USER_FOLLOWING_MAP_KEY) ? getFollowingCountByUser(user) : getFollowerCountByUser(user);

        // update
        redisCacheService.upsertCacheMapValueByKey(count, totalKey, user.getToken());
    }


    // likes Total 레디스에서 검색 후 존재하지 않으면 DB 검색 후 반환 검색
    public int getFollowerTotalCount(User user) {
        String key = RedisKeyDto.REDIS_USER_FOLLOWER_TOTAL_MAP_KEY;

        int count = 0;
            // redis 에 total 캐시가 있으면
        count = redisCacheService.getFollowerCount(key, user.getToken());

        count = count != 0 ? count : getFollowerCountByUser(user);
        // redis 에 저장이 안되어 있을경우 count 후 저장
        if (count == 0) {
            redisCacheService.upsertCacheMapValueByKey(count, key, user.getToken());
        }
        return count;
    }

    public int getFollowingTotalCount(User user) {
        String key = RedisKeyDto.REDIS_USER_FOLLOWING_TOTAL_MAP_KEY;
        // redis 에 total 캐시가 있으면
        int count = 0;
        count = redisCacheService.getFollowingCount(key, user.getToken());
        count = count != 0 ? count :getFollowingCountByUser(user);
        // redis 에 저장이 안되어 있을경우 count 후 저장
        if (count == 0) {
            redisCacheService.upsertCacheMapValueByKey(count, key, user.getToken());
        }
        return count;
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

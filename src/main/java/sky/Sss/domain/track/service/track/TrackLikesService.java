package sky.Sss.domain.track.service.track;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.common.like.LikeSimpleInfoDto;
import sky.Sss.domain.track.entity.track.SsbTrackLikes;
import sky.Sss.domain.track.repository.track.TrackLikesRepository;
import sky.Sss.domain.user.dto.UserSimpleInfoDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisCacheService;


/**
 * 트랙 좋아요 관련 service
 */
@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class TrackLikesService {


    private final TrackLikesRepository trackLikesRepository;
    private final RedisCacheService redisCacheService;
    private final UserQueryService userQueryService;

    /**
     * 좋아요 취소
     */
    @Caching(evict = {
        @CacheEvict(value = {RedisKeyDto.REDIS_USER_TRACK_LIKES_LIST_KEY}, key = "#user.userId")
    })
    @Transactional
    public void cancelLikes(long trackId, String token, User user) {
        // 사용자 검색
        SsbTrackLikes ssbTrackLikes = getEntityTrackLike(trackId, user);
        delete(ssbTrackLikes);

        String key = getLikeKey(token);

        // likesMap 안에 들어갈 user 를 검색하는 key
        String subUserKey = user.getToken();
        // 좋아요 수 업로드
        redisCacheService.removeCacheMapValueByKey(new UserSimpleInfoDto(), key, subUserKey);
    }

    /**
     * Track 좋아요 추가
     */

    @Caching(evict = {
        @CacheEvict(value = {RedisKeyDto.REDIS_USER_TRACK_LIKES_LIST_KEY}, key = "#user.userId")
    })
    @Transactional
    public void addLikes(long id, String token, User user) {
        // 저장
        SsbTrackLikes ssbTrackLikes = SsbTrackLikes.create(user);

        SsbTrackLikes.updateTrack(ssbTrackLikes, id);

        trackLikesRepository.save(ssbTrackLikes);

        String key = getLikeKey(token);

        // likesMap 안에 들어갈 user 를 검색하는 key
        String subUserKey = user.getToken();

        // redis 좋아요 수 업로드
        // redis 에 저장
        redisCacheService.upsertCacheMapValueByKey(new UserSimpleInfoDto(user), key, subUserKey);
    }

    /**
     * 없을시에 IllegalArgumentException
     *
     * @return
     */
    public SsbTrackLikes getEntityTrackLike(long trackId, User user) {
        return trackLikesRepository.findBySsbTrackIdAndUser(trackId, user)
            .orElseThrow(IllegalArgumentException::new);
    }

    /**
     * 없을시에 IllegalArgumentException
     *
     * @param trackToken
     * @param user
     * @return
     */
    public Optional<SsbTrackLikes> getLikeCacheFromOrDbByToken(String trackToken, User user) {
        UserSimpleInfoDto cacheMapBySubKey = redisCacheService.getCacheMapValueBySubKey(UserSimpleInfoDto.class,
            user.getToken(),
            getLikeKey(trackToken));
        if (cacheMapBySubKey == null) {
            return trackLikesRepository.findByLikeByTrackToken(trackToken, user.getId());
        }
        return Optional.of(SsbTrackLikes.create(user));
    }

    public Optional<SsbTrackLikes> findOneAsOpt(long trackId, User user) {
//        RedisTrackDto redisTrackDto = redisCacheService.getCacheMapValueBySubKey(RedisTrackDto.class,
//            String.valueOf(trackId),
//            RedisKeyDto.REDIS_TRACKS_INFO_MAP_KEY);

//        if (redisTrackDto == null) {
            return trackLikesRepository.findBySsbTrackIdAndUser(trackId, user);
//        }
//        User ownerUser = userQueryService.findOne(redisTrackDto.getUid(), Enabled.ENABLED);
//        SsbTrack ssbTrack = SsbTrack.redisTrackDtoToSsbTrack(redisTrackDto, ownerUser);
//        return getLikeCacheFromOrDbByToken(ssbTrack.getToken(), user);
    }

    public Optional<SsbTrackLikes> findOneAsOptByToken(String trackToken, User user) {
        return getLikeCacheFromOrDbByToken(trackToken, user);
    }
    /**
     * like 취소
     *
     * @return
     */
    @Transactional
    public void delete(SsbTrackLikes ssbTrackLikes) {
        trackLikesRepository.delete(ssbTrackLikes);
    }

    // likes Total 레디스에서 검색 후 존재하지 않으면 DB 검색 후 반환 검색
    public int getTotalCount(String trackToken) {
        String key = RedisKeyDto.REDIS_TRACK_LIKES_MAP_KEY + trackToken;
        // redis 에 total 캐시가 있으면
        int count = redisCacheService.getTotalCountByKey(new HashMap<>(), key);

        if (count > 0) {
            return count;
        }
//        count = count != 0 ? count : getListByToken(trackToken);
        // redis 에 저장이 안되어 있을경우 count 후 저장
        List<SsbTrackLikes> listByToken = getListByToken(trackToken);
        // 검색 후 map Data 생성후 추가
        Map<String, UserSimpleInfoDto> addDataMap = listByToken.stream()
            .collect(Collectors.toMap(ssbTrackLikes -> ssbTrackLikes.getUser().getToken(),
                ssbTrackLikes -> new UserSimpleInfoDto(ssbTrackLikes.getUser())));
        redisCacheService.upsertAllCacheMapValuesByKey(addDataMap, key);
        return listByToken.size();
    }

    // likes Total 레디스에서 검색 후 존재하지 않으면 DB 검색 후 반환 검색
    public List<SsbTrackLikes> getLikeListByTokens(Set<String> tokens) {
        return trackLikesRepository.getLikeListByTokens(tokens);
    }


    // likes Total 레디스에서 검색 후 존재하지 않으면 DB 검색 후 반환 검색
    public List<LikeSimpleInfoDto> getLikeSimpleListByTokens(Set<String> tokens) {
        return trackLikesRepository.getLikeSimpleListByTokens(tokens);
    }

    public List<User> getUserList(String trackToken) {
        return trackLikesRepository.getUserList(trackToken);
    }


    public List<Long> getUserLikedTrackIds(User user, Sort sort) {
        return trackLikesRepository.getUserLikedTrackIds(user,sort);
    }


    private List<SsbTrackLikes> getListByToken(String token) {
        return trackLikesRepository.getTrackLikesByToken(token);
    }

    public String getLikeKey(String token) {
        return RedisKeyDto.REDIS_TRACK_LIKES_MAP_KEY + token;
    }
}

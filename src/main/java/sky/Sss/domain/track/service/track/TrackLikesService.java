package sky.Sss.domain.track.service.track;


import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.SsbTrackLikes;
import sky.Sss.domain.track.repository.track.TrackLikesRepository;
import sky.Sss.domain.user.dto.UserSimpleInfoDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisCacheService;
import sky.Sss.global.redis.service.RedisQueryService;


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

    /**
     * Track 좋아요 추가
     */
    @Transactional
    public void addLike(SsbTrackLikes ssbTrackLikes) {
        SsbTrackLikes save = trackLikesRepository.save(ssbTrackLikes);
        String key = getLikeKey(save.getSsbTrack());

        // likesMap 안에 들어갈 user 를 검색하는 key
        String subUserKey = ssbTrackLikes.getUser().getUserId();
        redisCacheService.upsertCacheMapValueByKey(new UserSimpleInfoDto(ssbTrackLikes.getUser()), key, subUserKey);
    }

    /**
     * 좋아요 취소
     */
    @Transactional
    public void cancelLike(SsbTrack ssbTrack ,User user) {
        SsbTrackLikes ssbTrackLikes = findOne(ssbTrack, user);

        deleteByEntity(ssbTrackLikes);

        String key = getLikeKey(ssbTrack);

        // likesMap 안에 들어갈 user 를 검색하는 key
        String subUserKey = user.getUserId();

        redisCacheService.removeCacheMapValueByKey(new UserSimpleInfoDto(), key, subUserKey);
    }

    /**
     *
     * 없을시에 IllegalArgumentException
     * @param ssbTrack
     * @param user
     * @return
     */
    public SsbTrackLikes findOne (SsbTrack ssbTrack,User user) {
        SsbTrackLikes ssbTrackLikes = trackLikesRepository.findBySsbTrackAndUser(ssbTrack, user)
            .orElseThrow(() -> new IllegalArgumentException());
        return ssbTrackLikes;
    }

    /**
     * like 취소
     *
     * @return
     */
    @Transactional
    public void deleteByEntity (SsbTrackLikes ssbTrackLikes) {
        trackLikesRepository.delete(ssbTrackLikes);

    }

    /**
     * 좋아요 눌렀는지 확인
     */
    public boolean existsLikes(SsbTrack ssbTrack, User user) {
        String key = getLikeKey(ssbTrack);
        // redis 에 있는지 확인
        if (redisCacheService.hasRedis(key)) {
            return redisCacheService.existsByUserId(user, key);
        }
        Optional<SsbTrackLikes> trackLikesOptional = trackLikesRepository.findBySsbTrackAndUser(ssbTrack, user);

        // 만약 레디스에는 없고 디비에는 있으면
        if (!trackLikesOptional.isEmpty()) {
            redisCacheService.upsertCacheMapValueByKey(new UserSimpleInfoDto(user), key, user.getUserId());
        }
        return !trackLikesOptional.isEmpty();
    }

    // likes Total 업데이트
    public void updateTotalCount(String token) {
        // likes Size 를 구하긴 위한 key 값
        String key = RedisKeyDto.REDIS_TRACK_LIKES_KEY + token;

        String totalKey = RedisKeyDto.REDIS_TRACK_LIKES_TOTAL_KEY;

        // redis 에서 총 size 검색
        Integer count = redisCacheService.getRedisTotalCount(key);

        count = count != null ? count : getCountByTrackToken(token);
        redisCacheService.upsertCacheMapValueByKey(count, totalKey, token);
    }

    // likes Total 레디스에서 검색 후 존재하지 않으면 DB 검색 후 반환 검색
    public int getTotalCount(String token) {
        String key = RedisKeyDto.REDIS_TRACK_LIKES_TOTAL_KEY;
        // redis 에 total 캐시가 있으면
        Integer count = redisCacheService.getCount(key, token);

        count = count != null ? count : getCountByTrackToken(token);
        // redis 에 저장이 안되어 있을경우 count 후 저장
        if (count == null) {
            redisCacheService.upsertCacheMapValueByKey(count, key, token);
        }
        return count;
    }

    private Integer getCountByTrackToken(String token) {
        return trackLikesRepository.countByTrackToken(token);
    }

    public String getLikeKey(SsbTrack ssbTrack) {
        return RedisKeyDto.REDIS_TRACK_LIKES_KEY + ssbTrack.getToken();
    }
}

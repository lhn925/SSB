package sky.Sss.domain.track.service.track;


import java.util.HashMap;
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
    public void addLikes(SsbTrack ssbTrack, User user) {
        boolean isLikes = existsLikes(ssbTrack, user);
        if (isLikes) {
            // 좋아요가 있는지 확인
            // 좋아요가 이미 있는 경우 예외 처리
            throw new IllegalArgumentException();
        }
        // 저장
        SsbTrackLikes ssbTrackLikes = SsbTrackLikes.create(user, ssbTrack);

        SsbTrackLikes save = trackLikesRepository.save(ssbTrackLikes);

        String key = getLikeKey(save.getSsbTrack());

        // likesMap 안에 들어갈 user 를 검색하는 key
        String subUserKey = ssbTrackLikes.getUser().getToken();

        // redis 좋아요 수 업로드
//        updateTotalCount(ssbTrack.getToken());
        // redis 에 저장
        redisCacheService.upsertCacheMapValueByKey(new UserSimpleInfoDto(ssbTrackLikes.getUser()), key, subUserKey);
    }

    /**
     * 좋아요 취소
     */
    @Transactional
    public void cancelLikes(SsbTrack ssbTrack ,User user) {
        // 사용자 검색
        // 좋아요가 있는지 확인
        // 좋아요가 없는데 취소하는 경우 예외 처리
        SsbTrackLikes ssbTrackLikes = findOne(ssbTrack, user);;

        delete(ssbTrackLikes);

        String key = getLikeKey(ssbTrack);

        // likesMap 안에 들어갈 user 를 검색하는 key
        String subUserKey = user.getToken();
        // 좋아요 수 업로드
//        updateTotalCount(ssbTrack.getToken());
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
        return trackLikesRepository.findBySsbTrackAndUser(ssbTrack, user)
            .orElseThrow(IllegalArgumentException::new);
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
        if (trackLikesOptional.isPresent()) {
            redisCacheService.upsertCacheMapValueByKey(new UserSimpleInfoDto(user), key, user.getToken());
        }
        return trackLikesOptional.isPresent();
    }
/*
    *//**
     *
     *
     * @param trackToken
     *//*
    // likes Total 업데이트
    public void updateTotalCount(String trackToken) {
        // likes Size 를 구하긴 위한 key 값
        String key = RedisKeyDto.REDIS_TRACK_LIKES_MAP_KEY + trackToken;

        String totalKey = RedisKeyDto.REDIS_TRACK_LIKES_TOTAL_MAP_KEY;

        // redis 에서 총 size 검색
        int count = redisCacheService.getTotalCountByKey(new HashMap<>(),key);

        count = count != 0 ? count : getCountByTrackToken(trackToken);
        redisCacheService.upsertCacheMapValueByKey(count, totalKey, trackToken);
    }*/

    // likes Total 레디스에서 검색 후 존재하지 않으면 DB 검색 후 반환 검색
    public int getTotalCount(String trackToken) {
        String key = RedisKeyDto.REDIS_TRACK_LIKES_MAP_KEY + trackToken;
        // redis 에 total 캐시가 있으면
        int count = redisCacheService.getTotalCountByKey(new HashMap<>(), key);

        count = count != 0 ? count : getCountByTrackToken(trackToken);
        // redis 에 저장이 안되어 있을경우 count 후 저장
        if (count == 0) {
            redisCacheService.upsertCacheMapValueByKey(count, key, trackToken);
        }
        return count;
    }

    private Integer getCountByTrackToken(String token) {
        return trackLikesRepository.countByTrackToken(token);
    }

    public String getLikeKey(SsbTrack ssbTrack) {
        return RedisKeyDto.REDIS_TRACK_LIKES_MAP_KEY + ssbTrack.getToken();
    }
}

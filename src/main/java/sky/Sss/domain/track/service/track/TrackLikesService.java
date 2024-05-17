package sky.Sss.domain.track.service.track;


import com.fasterxml.jackson.core.type.TypeReference;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.SsbTrackLikes;
import sky.Sss.domain.track.repository.track.TrackLikesRepository;
import sky.Sss.domain.user.dto.UserSimpleInfoDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Status;
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
    private final TrackQueryService trackQueryService;


    /**
     * Track 좋아요 추가
     */
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
//        updateTotalCount(ssbTrack.getToken());
        // redis 에 저장
        redisCacheService.upsertCacheMapValueByKey(new UserSimpleInfoDto(user), key, subUserKey);
    }
    /**
     * 좋아요 취소
     */
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
     * @param ssbTrack
     * @param user
     * @return
     */
    public SsbTrackLikes getTrackLikeCacheFromOrDbByToken(SsbTrack ssbTrack, User user) {
        TypeReference<HashMap<String,UserSimpleInfoDto>> typeReference = new TypeReference<>() {};

        HashMap<String, UserSimpleInfoDto> simpleMap = redisCacheService.getData(getLikeKey(ssbTrack.getToken()), typeReference);

        if (simpleMap == null || simpleMap.containsKey(user.getToken())) {
            return trackLikesRepository.findBySsbTrackAndUser(ssbTrack, user)
                .orElseThrow(IllegalArgumentException::new);
        }
        return SsbTrackLikes.create(user);
    }


    public Optional<SsbTrackLikes> findOneAsOpt(long trackId, User user) {

        SsbTrack ssbTrack = trackQueryService.findById(trackId, Status.ON);

        return Optional.ofNullable(getTrackLikeCacheFromOrDbByToken(ssbTrack, user));
    }
    public List<SsbTrackLikes> findOneAsOpt(Set<Long> ids, User user) {
//        return trackLikesRepository.findBySsbTrackIdAndUser(0L, user);
        return null;
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

        count = count != 0 ? count : getCountByTrackToken(trackToken);
        // redis 에 저장이 안되어 있을경우 count 후 저장
        if (count == 0) {
            redisCacheService.upsertCacheMapValueByKey(count, key, trackToken);
        }
        return count;
    }

    public List<User> getUserList(String trackToken) {

        return trackLikesRepository.getUserList(trackToken);
    }

    private Integer getCountByTrackToken(String token) {
        return trackLikesRepository.countByTrackToken(token);
    }

    public String getLikeKey(String token) {
        return RedisKeyDto.REDIS_TRACK_LIKES_MAP_KEY + token;
    }
}

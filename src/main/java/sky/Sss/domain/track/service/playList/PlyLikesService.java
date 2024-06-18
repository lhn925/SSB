package sky.Sss.domain.track.service.playList;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.common.like.LikedRedisDto;
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;
import sky.Sss.domain.track.entity.playList.SsbPlyLikes;
import sky.Sss.domain.track.entity.track.SsbTrackLikes;
import sky.Sss.domain.track.repository.playList.PlyLikesRepository;
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
public class PlyLikesService {


    private final PlyLikesRepository plyLikesRepository;
    private final RedisCacheService redisCacheService;

    /**
     * Track 좋아요 추가
     */
    @Transactional
    public LikedRedisDto addLikes(Long id, String token, User user) {
        SsbPlyLikes ssbPlyLikes = SsbPlyLikes.create(user);
        SsbPlyLikes.updateSettings(ssbPlyLikes, id);

        plyLikesRepository.save(ssbPlyLikes);
        String key = getLikeKey(token);

        // likesMap 안에 들어갈 user 를 검색하는 key
        String subUserKey = ssbPlyLikes.getUser().getToken();
        redisCacheService.upsertCacheMapValueByKey(new UserSimpleInfoDto(ssbPlyLikes.getUser()), key, subUserKey);

        return new LikedRedisDto(ssbPlyLikes.getId(), ssbPlyLikes.getSsbPlayListSettings().getId(),
            ssbPlyLikes.getUser().getId(), ssbPlyLikes.getCreatedDateTime());
    }

    /**
     * 좋아요 취소
     */
    @Transactional
    public void cancelLikes(long plyId, String plyToken, User user) {
        // 좋아요가 있는지 확인
        SsbPlyLikes ssbTrackLikes = findOne(plyId, user);

        deleteByEntity(ssbTrackLikes);

        String key = getLikeKey(plyToken);

        // likesMap 안에 들어갈 user 를 검색하는 key
        String subUserKey = user.getToken();

        redisCacheService.removeCacheMapValueByKey(new UserSimpleInfoDto(), key, subUserKey);
    }

    /**
     * 없을시에 IllegalArgumentException
     *
     * @param user
     * @return
     */
    public SsbPlyLikes findOne(SsbPlayListSettings ssbPlayListSettings, User user) {
        return plyLikesRepository.findByPlyIdAndUser(ssbPlayListSettings, user)
            .orElseThrow(IllegalArgumentException::new);

    }

    /**
     * 없을시에 IllegalArgumentException
     *
     * @param user
     * @return
     */
    public SsbPlyLikes findOne(long plyId, User user) {
        return plyLikesRepository.findBySettingsIdAndUser(plyId, user)
            .orElseThrow(IllegalArgumentException::new);

    }

    /**
     * 없을시에 IllegalArgumentException
     *
     * @param user
     * @return
     */
    public Optional<SsbPlyLikes> findOneAsOpt(long plyId, User user) {
        return plyLikesRepository.findBySettingsIdAndUser(plyId, user);

    }

    public LikedRedisDto getLikedRedisDto(long trackId, User user) {

        Optional<SsbPlyLikes> oneAsOpt = findOneAsOpt(trackId, user);
        if (oneAsOpt.isPresent()) {
            SsbPlyLikes ssbPlyLikes = oneAsOpt.get();
            return new LikedRedisDto(ssbPlyLikes.getId(), ssbPlyLikes.getSsbPlayListSettings().getId(),
                ssbPlyLikes.getUser().getId(), ssbPlyLikes.getCreatedDateTime());
        }
        return null;
    }


    /**
     * like 취소
     *
     * @return
     */
    @Transactional
    public void deleteByEntity(SsbPlyLikes ssbPlyLikes) {
        plyLikesRepository.delete(ssbPlyLikes);

    }

    /**
     * 좋아요 눌렀는지 확인
     */
    public boolean existsLikes(String token, long id, User user) {
        String key = getLikeKey(token);
        // redis 에 있는지 확인
        if (redisCacheService.hasRedis(key)) {
            return redisCacheService.existsBySubKey(user.getToken(), key);
        }
        Optional<SsbPlyLikes> plyLikesOptional = plyLikesRepository.findBySettingsIdAndUser(id, user);

        // 만약 레디스에는 없고 디비에는 있으면
        if (plyLikesOptional.isPresent()) {
            redisCacheService.upsertCacheMapValueByKey(new UserSimpleInfoDto(user), key, user.getToken());
        }
        return plyLikesOptional.isPresent();
    }


/*    // likes Total 업데이트
    public void updateTotalCount(String token) {
        // likes Size 를 구하긴 위한 key 값
        String key = RedisKeyDto.REDIS_PLY_LIKES_MAP_KEY + token;

        String totalKey = RedisKeyDto.REDIS_PLY_LIKES_TOTAL_MAP_KEY;

        int count = redisCacheService.getTotalCountByKey(new HashMap<>(),key);

        count = count != 0 ? count : getPlyCount(token);
        redisCacheService.upsertCacheMapValueByKey(count, totalKey, token);
    }*/

    // likes Total 조회수 검색
    public int getTotalCount(String token) {
        String key = RedisKeyDto.REDIS_PLY_LIKES_MAP_KEY + token;
        // redis 에 total 캐시가 있으면
        int count = redisCacheService.getTotalCountByKey(new HashMap<>(), key);

        if (count > 0) {
            return count;
        }
//        count = count != 0 ? count : getPlyListByToken(token);
        // redis 에 저장이 안되어 있을경우 count 후 저장
        List<SsbPlyLikes> plyListByToken = getPlyListByToken(token);
        Map<String, UserSimpleInfoDto> plyLikeMap = plyListByToken.stream()
            .collect(Collectors.toMap(plyToken -> plyToken.getUser().getToken(),
                plyToken -> new UserSimpleInfoDto(plyToken.getUser())));
        redisCacheService.upsertAllCacheMapValuesByKey(plyLikeMap, key);
        return count;
    }

    public List<User> getUserList(String replyToken) {
        return plyLikesRepository.getUserList(replyToken);
    }

    private List<SsbPlyLikes> getPlyListByToken(String token) {
        return plyLikesRepository.getListPlyByToken(token);
    }


    private String getLikeKey(String token) {
        return RedisKeyDto.REDIS_PLY_LIKES_MAP_KEY + token;
    }
}

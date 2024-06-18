package sky.Sss.domain.track.service.playList.reply;


import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.common.like.LikedRedisDto;
import sky.Sss.domain.track.entity.playList.reply.SsbPlyReply;
import sky.Sss.domain.track.entity.playList.reply.SsbPlyReplyLikes;
import sky.Sss.domain.track.entity.track.reply.SsbTrackReplyLikes;
import sky.Sss.domain.track.repository.playList.reply.PlyReplyLikesRepository;
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
public class PlyReplyLikesService {


    private final PlyReplyLikesRepository plyReplyLikesRepository;
    private final RedisCacheService redisCacheService;

    /**
     * Track 좋아요 추가
     */
    @Transactional
    public LikedRedisDto addLikes(long id, String token, User user) {
        // 저장
        SsbPlyReplyLikes ssbPlyReplyLikes = SsbPlyReplyLikes.create(user);

        SsbPlyReplyLikes.updateReply(ssbPlyReplyLikes,id);

        plyReplyLikesRepository.save(ssbPlyReplyLikes);

        String key = getLikeKey(token);

        // likesMap 안에 들어갈 user 를 검색하는 key
        String subUserKey = user.getToken();

        // redis 좋아요 수 업로드
//        updateTotalCount(ssbTrack.getToken());
        // redis 에 저장
        redisCacheService.upsertCacheMapValueByKey(new UserSimpleInfoDto(user), key, subUserKey);

        return new LikedRedisDto(ssbPlyReplyLikes.getId(), ssbPlyReplyLikes.getSsbPlyReply().getId(),
            ssbPlyReplyLikes.getUser().getId(), ssbPlyReplyLikes.getCreatedDateTime());
    }

    /**
     * 좋아요 취소
     */
    @Transactional
    public void cancelLikes(long replyId, String replyToken, User user) {
        // 사용자 검색
        SsbPlyReplyLikes ssbTrackReplyLikes = findOne(replyId, user);
        delete(ssbTrackReplyLikes);

        String key = getLikeKey(replyToken);

        // likesMap 안에 들어갈 user 를 검색하는 key
        String subUserKey = user.getToken();
        // 좋아요 수 업로드
        redisCacheService.removeCacheMapValueByKey(new UserSimpleInfoDto(), key, subUserKey);
    }


    /**
     * 없을시에 IllegalArgumentException
     *
     * @param user
     * @return
     */
    public SsbPlyReplyLikes findOne(SsbPlyReply ssbPlyReply, User user) {
        return plyReplyLikesRepository.findBySsbPlyReplyAndUser(ssbPlyReply, user)
            .orElseThrow(IllegalArgumentException::new);
    }
    /**
     * 없을시에 IllegalArgumentException
     *
     * @return
     */
    public SsbPlyReplyLikes findOne(long replyId, User user) {
        return plyReplyLikesRepository.findBySsbPlyReplyIdAndUser(replyId, user)
            .orElseThrow(IllegalArgumentException::new);
    }


    /**
     * 없을시에 IllegalArgumentException
     *
     * @return
     */
    public Optional<SsbPlyReplyLikes> findOneAsOpt(long replyId, User user) {
        return plyReplyLikesRepository.findBySsbPlyReplyIdAndUser(replyId, user);
    }
    public LikedRedisDto getLikedRedisDto(long trackId, User user) {
        Optional<SsbPlyReplyLikes> oneAsOpt = findOneAsOpt(trackId, user);
        if (oneAsOpt.isPresent()) {
            SsbPlyReplyLikes ssbPlyReplyLikes = oneAsOpt.get();
            return new LikedRedisDto(ssbPlyReplyLikes.getId(), ssbPlyReplyLikes.getSsbPlyReply().getId(),
                ssbPlyReplyLikes.getUser().getId(), ssbPlyReplyLikes.getCreatedDateTime());
        }
        return null;
    }

    /**
     * like 취소
     *
     * @return
     */
    @Transactional
    public void delete(SsbPlyReplyLikes ssbPlyReplyLikes) {
        plyReplyLikesRepository.delete(ssbPlyReplyLikes);
    }

    /**
     * 좋아요 눌렀는지 확인
     */
    public boolean existsLikes(String replyToken, long replyId, User user) {
        String key = getLikeKey(replyToken);
        // redis 에 있는지 확인
        if (redisCacheService.hasRedis(key)) {
            return redisCacheService.existsBySubKey(user.getToken(), key);
        }
        Optional<SsbPlyReplyLikes> replyLikesOptional = plyReplyLikesRepository.findBySsbPlyReplyIdAndUser(replyId, user);

        // 만약 레디스에는 없고 디비에는 있으면
        if (replyLikesOptional.isPresent()) {
            redisCacheService.upsertCacheMapValueByKey(new UserSimpleInfoDto(user), key, user.getToken());
        }
        return replyLikesOptional.isPresent();
    }
    /*
     */




    public List<User> getUserList(String replyToken) {
        return plyReplyLikesRepository.getUserList(replyToken);
    }

    // likes Total 레디스에서 검색 후 존재하지 않으면 DB 검색 후 반환 검색
    public int getTotalCount(String replyToken) {
        String key = getLikeKey(replyToken);
        // redis 에 total 캐시가 있으면
        int count = redisCacheService.getTotalCountByKey(new HashMap<>(), key);

        count = count != 0 ? count : getCountByTrackToken(replyToken);
        // redis 에 저장이 안되어 있을경우 count 후 저장
        if (count == 0) {
            redisCacheService.upsertCacheMapValueByKey(count, key, replyToken);
        }
        return count;
    }

    private Integer getCountByTrackToken(String token) {
        return plyReplyLikesRepository.countByReplToken(token);
    }

    public String getLikeKey(String token) {
        return RedisKeyDto.REDIS_PLY_REPLY_LIKES_MAP_KEY + token;
    }
}

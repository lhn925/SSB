package sky.Sss.domain.track.service.track.reply;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.common.like.LikedRedisDto;
import sky.Sss.domain.track.entity.track.SsbTrackLikes;
import sky.Sss.domain.track.entity.track.reply.SsbTrackReply;
import sky.Sss.domain.track.entity.track.reply.SsbTrackReplyLikes;
import sky.Sss.domain.track.repository.track.reply.TrackReplyLikesRepository;
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
public class TrackReplyLikesService {


    private final TrackReplyLikesRepository trackReplyLikesRepository;
    private final RedisCacheService redisCacheService;

    /**
     * Track 좋아요 추가
     */
    @Transactional
    public void addLikes(long id, String token, User user) {
        // 저장
        SsbTrackReplyLikes ssbTrackReplyLikes = SsbTrackReplyLikes.create(user);

        SsbTrackReplyLikes.updateReply(ssbTrackReplyLikes, id);

        trackReplyLikesRepository.save(ssbTrackReplyLikes);

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
    public void cancelLikes(long replyId, String replyToken, User user) {
        // 사용자 검색
        SsbTrackReplyLikes ssbTrackReplyLikes = findOneByTrackId(replyId, user);
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
     * @param ssbTrackReply
     * @param user
     * @return
     */
    public SsbTrackReplyLikes findOneByTrackId(SsbTrackReply ssbTrackReply, User user) {
        return trackReplyLikesRepository.findBySsbTrackReplyAndUser(ssbTrackReply, user)
            .orElseThrow(IllegalArgumentException::new);
    }

    /**
     * 없을시에 IllegalArgumentException
     *
     * @return
     */
    public SsbTrackReplyLikes findOneByTrackId(long trackId, User user) {
        return trackReplyLikesRepository.findBySsbTrackReplyIdAndUser(trackId, user)
            .orElseThrow(IllegalArgumentException::new);
    }


    /**
     * like 취소
     *
     * @return
     */
    @Transactional
    public void delete(SsbTrackReplyLikes ssbTrackReplyLikes) {
        trackReplyLikesRepository.delete(ssbTrackReplyLikes);
    }
    public Optional<SsbTrackReplyLikes> findOneAsOpt(long replyId, User user) {
        return trackReplyLikesRepository.findBySsbTrackReplyIdAndUser(
            replyId, user);
    }

    public LikedRedisDto getLikedRedisDto(long trackId, User user) {
        Optional<SsbTrackReplyLikes> oneAsOpt = findOneAsOpt(trackId, user);
        if (oneAsOpt.isPresent()) {
            SsbTrackReplyLikes ssbTrackReplyLikes = oneAsOpt.get();
            return new LikedRedisDto(ssbTrackReplyLikes.getId(), ssbTrackReplyLikes.getSsbTrackReply().getId(),
                ssbTrackReplyLikes.getUser().getId(), ssbTrackReplyLikes.getCreatedDateTime());
        }
        return null;
    }


    /*
     */



    public List<User> getUserList(String replyToken) {
        return trackReplyLikesRepository.getUserList(replyToken);
    }
    public List<SsbTrackReplyLikes> findAll(String replyToken) {
        return trackReplyLikesRepository.findAll(replyToken);
    }

    private Integer getCountByTrackToken(String token) {
        return trackReplyLikesRepository.countByReplyToken(token);
    }

    public String getLikeKey(String token) {
        return RedisKeyDto.REDIS_TRACK_REPLY_LIKES_MAP_KEY + token;
    }
}

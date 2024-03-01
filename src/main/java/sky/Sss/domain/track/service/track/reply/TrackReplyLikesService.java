package sky.Sss.domain.track.service.track.reply;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final ObjectMapper objectMapper;

    /**
     * Track 좋아요 추가
     */
    @Transactional
    public void addLikes(long id, String token, User user) {
        boolean isLikes = existsLikes(token, id, user);
        if (isLikes) {
            // 좋아요가 있는지 확인
            // 좋아요가 이미 있는 경우 예외 처리
            throw new IllegalArgumentException();
        }

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
        SsbTrackReplyLikes ssbTrackReplyLikes = findOne(replyId, user);
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
    public SsbTrackReplyLikes findOne(SsbTrackReply ssbTrackReply, User user) {
        return trackReplyLikesRepository.findBySsbTrackReplyAndUser(ssbTrackReply, user)
            .orElseThrow(IllegalArgumentException::new);
    }

    /**
     * 없을시에 IllegalArgumentException
     *
     * @return
     */
    public SsbTrackReplyLikes findOne(long trackId, User user) {
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

    /**
     * 좋아요 눌렀는지 확인
     */
    public boolean existsLikes(String replyToken, long replyId, User user) {
        String key = getLikeKey(replyToken);
        // redis 에 있는지 확인
        if (redisCacheService.hasRedis(key)) {
            return redisCacheService.existsByToken(user, key);
        }
        Optional<SsbTrackReplyLikes> replyLikesOptional = trackReplyLikesRepository.findBySsbTrackReplyIdAndUser(
            replyId, user);

        // 만약 레디스에는 없고 디비에는 있으면
        if (replyLikesOptional.isPresent()) {
            redisCacheService.upsertCacheMapValueByKey(new UserSimpleInfoDto(user), key, user.getToken());
        }
        return replyLikesOptional.isPresent();
    }
    /*
     */

//
//    // likes Total 레디스에서 검색 후 존재하지 않으면 DB 검색 후 반환 검색
//    public int getTotalCount(String replyToken) {
//        String key = getLikeKey(replyToken);
//        // redis 에 total 캐시가 있으면
//        int count = redisCacheService.getTotalCountByKey(new HashMap<>(), key);
//
//        // redis 에 저장이 안되어 있을경우 count 후 저장
//        if (count == 0) {
//            List<User> users = getUserList(replyToken);
//            if (!users.isEmpty()) {
//                count = users.size();
//                redisCacheService.updateCacheMapValueByKey(key, users);
//            }
//        }
//        return count;
//    }
//


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

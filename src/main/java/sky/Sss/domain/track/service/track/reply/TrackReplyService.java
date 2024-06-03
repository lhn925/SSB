package sky.Sss.domain.track.service.track.reply;


import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.common.TargetInfoDto;
import sky.Sss.domain.track.dto.common.ReplyRmInfoDto;
import sky.Sss.domain.track.dto.track.reply.RedisTrackReplyDto;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.reply.SsbTrackReply;
import sky.Sss.domain.track.exception.checked.SsbFileNotFoundException;
import sky.Sss.domain.track.repository.track.reply.TrackReplyRepository;
import sky.Sss.domain.user.utili.TokenUtil;
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisCacheService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrackReplyService {


    private final TrackReplyRepository trackReplyRepository;
    private final RedisCacheService redisCacheService;

    @Transactional
    public void addReply(SsbTrackReply ssbTrackReply, SsbTrack ssbTrack) {

        int maxOrder = 0;
        // 대댓글 일경우
        if (ssbTrackReply.getParentId() != 0) {
            // 대댓글을 달려고 하는 댓글이 있는지 확인
            SsbTrackReply parentReply = findOne(ssbTrackReply.getParentId(), ssbTrack);

            // 대댓글 달 경우 이게 최상위 댓글인지 확인
            // 0이 아니면 대댓글에 대댓글이여서 ParentId 를 넘겨주고 0인 경우에는 최상위 댓글 이라서 원래 ParentId 로 검색
            Long parentId = parentReply.getParentId() != 0 ? parentReply.getParentId() : ssbTrackReply.getParentId();
            // parentId가 변경이 됐을 경우에
            if (!Objects.equals(ssbTrackReply.getParentId(), parentId)) {
                SsbTrackReply.updateParentId(ssbTrackReply, parentId);
            }
            // 대댓글 순서 검색 후 저장
            maxOrder = findMaxOrderByParentId(ssbTrack, parentId) + 1;
        }

        String replyToken = TokenUtil.getToken();
        SsbTrackReply.updateReplyOrder(ssbTrackReply, maxOrder);
        SsbTrackReply.updateToken(ssbTrackReply, replyToken);

        trackReplyRepository.save(ssbTrackReply);
        String key = RedisKeyDto.REDIS_TRACK_REPLY_MAP_KEY + ssbTrack.getToken();

        redisCacheService.upsertCacheMapValueByKey(new RedisTrackReplyDto(ssbTrackReply), key, replyToken);

    }

    /**
     * 댓글 삭제
     *
     * @param ssbTrackReply
     * @param ssbTrack
     */
    @Transactional
    public void removeReply(SsbTrackReply ssbTrackReply, SsbTrack ssbTrack) {

        trackReplyRepository.delete(ssbTrackReply);

        String key = RedisKeyDto.REDIS_TRACK_REPLY_MAP_KEY + ssbTrack.getToken();
        redisCacheService.removeCacheMapValueByKey(new RedisTrackReplyDto(), key, ssbTrackReply.getToken());
    }

    /**
     * 대댓글 삭제
     */
    @Transactional
    public void removeReplies(List<ReplyRmInfoDto> replyRmDtoList, String trackToken) {
        List<Long> replyIdList = replyRmDtoList.stream().map(ReplyRmInfoDto::getReplyId).toList();
        trackReplyRepository.deleteAllByIdInBatch(replyIdList);
        String key = RedisKeyDto.REDIS_TRACK_REPLY_MAP_KEY + trackToken;
        // 캐쉬 삭제
        replyRmDtoList.forEach(reply -> {
            redisCacheService.removeCacheMapValueByKey(new RedisTrackReplyDto(), key, reply.getReplyToken());
        });
    }

    public List<SsbTrackReply> findByParentId(long parentId) {
        return trackReplyRepository.findAllByParentId(parentId);
    }

    public int findMaxOrderByParentId(SsbTrack ssbTrack, Long parentId) {
        return trackReplyRepository.findMaxOrderByParentId(parentId, ssbTrack);
    }

    public SsbTrackReply findOne(long id, SsbTrack ssbTrack) {
        return trackReplyRepository.findByIdAndSsbTrack(id, ssbTrack)
            .orElseThrow(IllegalArgumentException::new);
    }


    public List<ReplyRmInfoDto> getReplyRmInfoDtoList(long id, String token) {
        List<ReplyRmInfoDto> listAndSubReplies = trackReplyRepository.getReplyRmInfoDtoList(id, token);
        if (listAndSubReplies.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return listAndSubReplies;
    }

    // 그 댓글에 달린 대댓글 까지 모두 가져오는 쿼리 실행
    public List<SsbTrackReply> findListAndSubReplies(long id, String token) {
        List<SsbTrackReply> listAndSubReplies = trackReplyRepository.findListAndSubReplies(id, token);
        if (listAndSubReplies.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return listAndSubReplies;
    }

    public TargetInfoDto getTargetInfoDto(long id,String token) {
        return trackReplyRepository.getTargetInfoDto(id, token)
            .orElseThrow(SsbFileNotFoundException::new);
    }


}

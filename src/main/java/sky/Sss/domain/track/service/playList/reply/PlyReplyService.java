package sky.Sss.domain.track.service.playList.reply;


import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.common.TargetInfoDto;
import sky.Sss.domain.track.dto.common.ReplyRmInfoDto;
import sky.Sss.domain.track.dto.playlist.reply.RedisPlyReplyDto;
import sky.Sss.domain.track.dto.track.reply.RedisTrackReplyDto;
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;
import sky.Sss.domain.track.entity.playList.reply.SsbPlyReply;
import sky.Sss.domain.track.exception.checked.SsbFileNotFoundException;
import sky.Sss.domain.track.repository.playList.reply.PlyReplyRepository;
import sky.Sss.domain.user.utili.TokenUtil;
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisCacheService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlyReplyService {


    private final PlyReplyRepository plyReplyRepository;
    private final RedisCacheService redisCacheService;

    @Transactional
    public void addReply(SsbPlyReply ssbPlyReply, SsbPlayListSettings ssbPlayListSettings) {

        int maxOrder = 0;
        // 대댓글 일경우
        if (ssbPlyReply.getParentId() != 0) {
            // 대댓글을 달려고 하는 댓글이 있는지 확인
            SsbPlyReply parentReply = findOne(ssbPlyReply.getParentId(), ssbPlayListSettings);

            // 대댓글 달 경우 이게 최상위 댓글인지 확인
            // 0이 아니면 대댓글에 대댓글이여서 ParentId 를 넘겨주고 0인 경우에는 최상위 댓글 이라서 원래 ParentId 로 검색
            Long parentId = parentReply.getParentId() != 0 ? parentReply.getParentId() : ssbPlyReply.getParentId();
            // parentId가 변경이 됐을 경우에
            if (!Objects.equals(ssbPlyReply.getParentId(), parentId)) {
                SsbPlyReply.updateParentId(ssbPlyReply, parentId);
            }
            // 대댓글 순서 검색 후 저장
            maxOrder = findMaxOrderByParentId(ssbPlayListSettings, parentId) + 1;
        }

        String replyToken = TokenUtil.getToken();
        SsbPlyReply.updateReplyOrder(ssbPlyReply, maxOrder);
        SsbPlyReply.updateToken(ssbPlyReply, replyToken);

        plyReplyRepository.save(ssbPlyReply);

        String key = RedisKeyDto.REDIS_PLY_REPLY_MAP_KEY + ssbPlayListSettings.getToken();

        redisCacheService.upsertCacheMapValueByKey(new RedisPlyReplyDto(ssbPlyReply), key, replyToken);

    }

    /**
     * 댓글 삭제
     *
     * @param ssbPlyReply
     * @param SsbPlayListSettings
     */
    @Transactional
    public void removeReply(SsbPlyReply ssbPlyReply, SsbPlayListSettings SsbPlayListSettings) {

        plyReplyRepository.delete(ssbPlyReply);

        String key = RedisKeyDto.REDIS_PLY_REPLY_MAP_KEY + SsbPlayListSettings.getToken();
        redisCacheService.removeCacheMapValueByKey(new RedisTrackReplyDto(), key, ssbPlyReply.getToken());
    }

    /**
     * 대댓글 삭제
     */
    @Transactional
    public void removeReplies(List<ReplyRmInfoDto> replyRmInfoDtoList, String settingToken) {
        List<Long> replyIdList = replyRmInfoDtoList.stream().map(ReplyRmInfoDto::getReplyId).toList();

        plyReplyRepository.deleteAllByIdInBatch(replyIdList);

        String key = RedisKeyDto.REDIS_PLY_REPLY_MAP_KEY + settingToken;
        // 캐쉬 삭제
        replyRmInfoDtoList.forEach(reply -> {
            redisCacheService.removeCacheMapValueByKey(new RedisTrackReplyDto(), key, reply.getReplyToken());
        });
    }

    public List<SsbPlyReply> findByParentId(long parentId) {
        return plyReplyRepository.findAllByParentId(parentId);
    }

    public int findMaxOrderByParentId(SsbPlayListSettings SsbPlayListSettings, Long parentId) {
        return plyReplyRepository.findMaxOrderByParentId(parentId, SsbPlayListSettings);
    }

    public SsbPlyReply findOne(long id, SsbPlayListSettings SsbPlayListSettings) {
        return plyReplyRepository.findByIdAndSsbPlayListSettings(id, SsbPlayListSettings)
            .orElseThrow(IllegalArgumentException::new);
    }

    // 그 댓글에 달린 대댓글 까지 모두 가져오는 쿼리 실행
    public List<ReplyRmInfoDto> getReplyRmInfoDtoList(long id, String token) {
        List<ReplyRmInfoDto> listAndSubReplies = plyReplyRepository.getReplyRmInfoDtoList(id, token);
        if (listAndSubReplies.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return listAndSubReplies;
    }

    public TargetInfoDto getTargetInfoDto(long id,String token) {
        return plyReplyRepository.getTargetInfoDto(id, token)
            .orElseThrow(SsbFileNotFoundException::new);
    }
//
//    public List<SsbPlyReply> getReplyRmInfoDtoList(long id, String token) {
//        List<SsbPlyReply> listAndSubReplies = plyReplyRepository.findListAndSubReplies(id, token);
//        if (listAndSubReplies.isEmpty()) {
//            throw new IllegalArgumentException();
//        }
//        return listAndSubReplies;
//    }
}

package sky.Sss.domain.track.service.track;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.track.common.TrackInfoSimpleDto;
import sky.Sss.domain.track.dto.track.rep.TrackSearchInfoDto;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.service.common.LikesCommonService;
import sky.Sss.domain.track.service.common.RepostCommonService;
import sky.Sss.domain.track.service.track.reply.TrackReplyService;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.ContentsType;
import sky.Sss.domain.user.model.Status;
import sky.Sss.global.redis.service.RedisQueryService;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class TrackInfoService {


    private final TrackQueryService trackQueryService;
    private final TrackLikesService trackLikesService;
    private final LikesCommonService likesCommonService;
    private final RepostCommonService repostCommonService;
    private final TrackReplyService trackReplyService;
    private final RedisQueryService redisQueryService;

    /**
     *
     * 트랙 정보 및 좋아요수,댓글수,repost,재생 수 반환
     * @param trackIds
     * @param user
     * @return
     */
    public List<TrackSearchInfoDto> getTrackSearchInfoDtoList(Set<Long> trackIds, User user){
        List<SsbTrack> ssbTrackList = trackQueryService.getTrackListFromOrDbByIds(trackIds);
        List<String> tokens = ssbTrackList.stream().map(SsbTrack::getToken).toList();

        // 좋아요 수 반환
        Map<String, Integer> totalCountList = likesCommonService.getTotalCountList(tokens, ContentsType.TRACK);

        // 댓글 수


        // 재생 수


        // repost 수


        return null;
    }


}

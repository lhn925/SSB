package sky.Sss.domain.track.service.track;

import java.util.List;
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
    public List<TrackSearchInfoDto> getTrackSearchInfoDtoList(Set<Long> trackIds, User user){

        List<SsbTrack> trackListFromOrDbByIds = trackQueryService.getTrackListFromOrDbByIds(trackIds);







        return null;
    }


}

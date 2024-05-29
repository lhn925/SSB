package sky.Sss.domain.track.service.track;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.track.rep.TrackSearchInfoDto;
import sky.Sss.domain.track.service.common.RepostCommonService;
import sky.Sss.domain.track.service.track.reply.TrackReplyService;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class TrackSearchService {


    private final TrackQueryService trackQueryService;
    private final TrackLikesService trackLikesService;
    private final TrackReplyService trackReplyService;
    private final RepostCommonService repostCommonService;
    public List<TrackSearchInfoDto> getTrackSearchInfoDtoList(Set<Long> trackIds){




        return null;
    }


}

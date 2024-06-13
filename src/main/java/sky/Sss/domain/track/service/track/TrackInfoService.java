package sky.Sss.domain.track.service.track;

import java.util.ArrayList;
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
import sky.Sss.domain.track.service.common.ReplyCommonService;
import sky.Sss.domain.track.service.common.RepostCommonService;
import sky.Sss.domain.track.service.track.play.TrackAllPlayLogService;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.ContentsType;
import sky.Sss.domain.user.model.Status;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class TrackInfoService {


    private final TrackQueryService trackQueryService;
    private final LikesCommonService likesCommonService;
    private final ReplyCommonService replyCommonService;
    private final TrackAllPlayLogService trackAllPlayLogService;
    private final RepostCommonService repostCommonService;

    /**
     * 트랙 정보 및 좋아요수,댓글수,repost,재생 수 반환
     *
     * @param ids
     * @param user
     * @return
     */
    public List<TrackSearchInfoDto> getTrackSearchInfoList(Set<Long> ids, User user) {
        List<TrackSearchInfoDto> searchInfoList = new ArrayList<>();

        List<SsbTrack> trackInfoList = trackQueryService.searchTrackInfoByIds(ids, user,
            Status.ON);
        List<String> tokens = trackInfoList.stream().map(SsbTrack::getToken).toList();

        // 좋아요 수 반환
        Map<String, Integer> likeTotalMap = likesCommonService.getTotalCountMap(tokens, ContentsType.TRACK);

        // 댓글 수
        Map<String, Integer> replyTotalMap = replyCommonService.getTotalCountMap(tokens, ContentsType.TRACK);

        // 재생 수
        Map<String, Integer> playTotalMap = trackAllPlayLogService.getTotalCountMap(tokens);

        // repost 수
        Map<String, Integer> repostTotalMap = repostCommonService.getTotalCountMap(tokens, ContentsType.TRACK);

        for (SsbTrack track : trackInfoList) {
            String token = track.getToken();
            int likeCount = likeTotalMap.get(token);
            int replyCount = replyTotalMap.get(token);
            int playCount = playTotalMap.get(token);
            int repostCount = repostTotalMap.get(token);

            TrackInfoSimpleDto trackInfoSimpleDto = TrackInfoSimpleDto.create(track);

            boolean isOwner = user.getToken().equals(track.getUser().getToken());
            TrackInfoSimpleDto.updateIsOwner(trackInfoSimpleDto, isOwner);
            // owner 일경우
            if (isOwner) {
                TrackInfoSimpleDto.updateToken(trackInfoSimpleDto, token);
            }
            searchInfoList.add(
                new TrackSearchInfoDto(trackInfoSimpleDto, likeCount, replyCount, repostCount, playCount));
        }
        return searchInfoList;
    }


}

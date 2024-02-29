package sky.Sss.domain.track.service.track;


import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.track.TotalCountRepDto;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.exception.checked.SsbTrackAccessDeniedException;
import sky.Sss.domain.track.service.common.TrackCommonService;
import sky.Sss.domain.track.service.track.reply.TrackReplyService;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.UserPushMessages;
import sky.Sss.domain.user.model.ContentsType;
import sky.Sss.domain.user.model.PushMsgType;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.domain.user.service.push.UserPushMsgService;


/**
 * 트랙과 관련된 사용자 활동을 모아 놓은 Service
 */
@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class TrackActionService {

    private final TrackLikesService trackLikesService;
    private final TrackQueryService trackQueryService;
    private final UserQueryService userQueryService;
    private final UserPushMsgService userPushMsgService;
    private final TrackReplyService trackReplyService;
    private final TrackCommonService trackCommonService;


    /**
     * 좋아요 추가 후
     * 좋아요 수 반환
     *
     * @return
     * @throws IOException
     */
    @Transactional
    public TotalCountRepDto addLikes(Long id, String token) {
        // track 검색
        SsbTrack ssbTrack = trackQueryService.findOneJoinUser(id, token, Status.ON);
        // 사용자 검색
        User fromUser = userQueryService.findOne();
        // push 를 받을 사용자
        User toUser = ssbTrack.getUser();

        // like 추가
        trackLikesService.addLikes(ssbTrack, fromUser);

        // 총 likes count
        int totalLikesCount = this.getTotalLikesCount(ssbTrack.getToken());

        // userPushMessages 객체 생성
        UserPushMessages userPushMessages = UserPushMessages.create(toUser, fromUser, PushMsgType.LIKES,
            ContentsType.TRACK, ssbTrack.getId());
        try {
            // 같은 사용자 인지 확인
            if (!fromUser.getToken().equals(toUser.getToken())) {
                // userPushMessages Table insert
                userPushMsgService.addUserPushMsg(userPushMessages);
                // push messages
                userPushMsgService.sendOrCacheMessages(ContentsType.TRACK.getUrl() + ssbTrack.getId(),
                    ssbTrack.getTitle(),
                    toUser,
                    userPushMessages);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // Redis 알림 리스트에 추가
        return new TotalCountRepDto(totalLikesCount);
    }

    @Transactional
    public TotalCountRepDto cancelLike(Long trackId, String trackToken) {
        // 사용자 검색
        User user = userQueryService.findOne();
        // track 검색
        SsbTrack ssbTrack = trackQueryService.findOneJoinUser(trackId, trackToken, Status.ON);
        trackLikesService.cancelLikes(ssbTrack, user);

        int totalCount = this.getTotalLikesCount(ssbTrack.getToken());

        return new TotalCountRepDto(totalCount);
    }

    private static void checkPrivacy(SsbTrack ssbTrack, boolean isOwner) {
        if (ssbTrack.getIsPrivacy() && !isOwner) {
            throw new SsbTrackAccessDeniedException("track.error.forbidden", HttpStatus.FORBIDDEN);
        }
    }

    /*   public void updateLikesCount(SsbTrack ssbTrack) {
           trackLikesService.updateTotalCount(ssbTrack.getToken());
       }
   */
    public int getTotalLikesCount(String token) {
        return trackLikesService.getTotalCount(token);
    }
}

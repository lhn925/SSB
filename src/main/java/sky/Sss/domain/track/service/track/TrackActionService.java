package sky.Sss.domain.track.service.track;


import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.track.TotalCountRepDto;
import sky.Sss.domain.track.dto.track.reply.TrackReplyRmReqDto;
import sky.Sss.domain.track.dto.track.reply.TrackReplySaveReqDto;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.reply.SsbTrackReply;
import sky.Sss.domain.track.exception.checked.SsbTrackAccessDeniedException;
import sky.Sss.domain.track.service.track.reply.TrackReplyService;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.UserPushMessages;
import sky.Sss.domain.user.model.ContentsType;
import sky.Sss.domain.user.model.Enabled;
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


    /**
     * 리플 등록
     *
     * @param trackReplySaveReqDto
     */
    @Transactional
    public void addReply(TrackReplySaveReqDto trackReplySaveReqDto) {
        User user = userQueryService.findOne();
        Long trackId = trackReplySaveReqDto.getTrackId();
        String trackToken = trackReplySaveReqDto.getToken();

        SsbTrack ssbTrack = trackQueryService.findOneJoinUser(trackId, trackToken, Status.ON);

        User ownerUser = ssbTrack.getUser();
        // 댓글 작성자와 ssbTrack 작성자가 일치한지 확인
        boolean isOwner = ownerUser.getToken().equals(user.getToken());

        // 비공개인데 댓글을 달경우 isOwner 와 같이 체크 권한 있는지 확인 후 없으면 SsbTrackAccessDeniedException
        if (ssbTrack.getIsPrivacy() && !isOwner) {
            throw new SsbTrackAccessDeniedException("track.error.forbidden", HttpStatus.FORBIDDEN);
        }
        // ssbTrackReply 생성
        SsbTrackReply ssbTrackReply = SsbTrackReply.create(trackReplySaveReqDto, user, ssbTrack);

        // 댓글 등록
        trackReplyService.addReply(ssbTrackReply, ssbTrack);

        Set<String> userTagSet = trackReplySaveReqDto.getUserTagSet();
        // 작성자 태그 Set 에서 삭제
        userTagSet.remove(ownerUser.getUserName());
        // 자기 자신을 태그 한 경우 삭제
        userTagSet.remove(user.getUserName());

        Set<User> users = new HashSet<>();

        // 작성자가 아닌 경우
        if (!isOwner) {
            // 작성자 추가
            users.add(ssbTrack.getUser());
        }
        users.addAll(userQueryService.findUsersByUserNames(userTagSet, Enabled.ENABLED));

        String contents = ssbTrackReply.getContents();
        String linkUrl = ContentsType.TRACK.getUrl() + trackId + "/" + ssbTrackReply.getId();
        users.forEach(toUser -> {
            UserPushMessages userPushMessages = UserPushMessages.create(toUser, user, PushMsgType.REPLY,
                ContentsType.TRACK, ssbTrackReply.getId());
            userPushMsgService.addUserPushMsg(userPushMessages);
            userPushMsgService.sendOrCacheMessages(linkUrl, contents, toUser, userPushMessages);
        });
    }

    /**
     * 리플 삭제
     *
     * @param trackReplyRmReqDto
     */
    @Transactional
    public void deleteReply(TrackReplyRmReqDto trackReplyRmReqDto) {
//        SsbTrack ssbTrack = trackQueryService.findOne(trackReplyRmReqDto.getTrackId(),
//            trackReplyRmReqDto.getTrackToken(),
//            Status.ON);
        // 삭제를 요청한 사용자
        User user = userQueryService.findOne();
        // 댓글을 가져올때 대 댓글도 모두 검색 후 반환
        List<SsbTrackReply> ssbTrackReplies = trackReplyService.findListAndSubReplies(trackReplyRmReqDto.getReplyId(),
            trackReplyRmReqDto.getReplyToken());

        SsbTrackReply ssbTrackReply = ssbTrackReplies.stream()
            .filter(reply -> reply.getToken().equals(trackReplyRmReqDto.getReplyToken()))
            .findFirst().orElseThrow(
                IllegalArgumentException::new);

        SsbTrack ssbTrack = ssbTrackReply.getSsbTrack();

        // 요청자 아이디
        long uid = user.getId();
        // 트랙 작성 유저
        long trackOwnerUid = ssbTrack.getUser().getId();

        // 댓글 작성자 고유 아이디 추출
        long ownerUid = ssbTrackReply.getUser().getId();

        // 댓글 작성자도 아니고 트랙 작성자도 아닐시에
        // 권한 에러
        if (ownerUid != uid && trackOwnerUid != uid) {
            throw new SsbTrackAccessDeniedException("access.error.forbidden");
        }
//        List<SsbTrackReply> replies = trackReplyService.findByParentId(ssbTrackReply.getId());
        // 대댓글이 있을 경우에 전부 삭제
        trackReplyService.removeReplies(ssbTrackReplies, ssbTrack);

    }


    /*   public void updateLikesCount(SsbTrack ssbTrack) {
           trackLikesService.updateTotalCount(ssbTrack.getToken());
       }
   */
    public int getTotalLikesCount(String token) {
        return trackLikesService.getTotalCount(token);
    }
}

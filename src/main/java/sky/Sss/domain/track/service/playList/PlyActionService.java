package sky.Sss.domain.track.service.playList;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.playlist.reply.PlyReplyRmReqDto;
import sky.Sss.domain.track.dto.playlist.reply.PlyReplySaveReqDto;
import sky.Sss.domain.track.dto.track.TotalCountRepDto;
import sky.Sss.domain.track.dto.track.reply.TrackReplyRmReqDto;
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;
import sky.Sss.domain.track.entity.playList.reply.SsbPlyReply;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.reply.SsbTrackReply;
import sky.Sss.domain.track.exception.checked.SsbTrackAccessDeniedException;
import sky.Sss.domain.track.service.playList.reply.PlyReplyService;
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
public class PlyActionService {

    private final UserQueryService userQueryService;
    private final PlyLikesService plyLikesService;
    private final UserPushMsgService userPushMsgService;
    private final PlyQueryService plyQueryService;
    private final PlyReplyService plyReplyService;


    /**
     * playList 좋아요 추가 후 총 좋아요 수 반환
     */
    @Transactional
    public TotalCountRepDto addLikes(Long id, String token) {
        // track 검색
        SsbPlayListSettings ssbPlayListSettings = plyQueryService.findOneJoinUser(id, token, Status.ON);

        // 사용자 검색
        User fromUser = userQueryService.findOne();

        User toUser = ssbPlayListSettings.getUser();

        plyLikesService.addLikes(ssbPlayListSettings, fromUser);

        UserPushMessages userPushMessages = UserPushMessages.create(toUser, fromUser, PushMsgType.LIKES,
            ContentsType.PLAYLIST,
            ssbPlayListSettings.getId());

        // 같은 사용자인지 확인
        if (!fromUser.equals(toUser)) {
            userPushMsgService.addUserPushMsg(userPushMessages);
            // 현재 유저가 접속 되어 있는지 확인
            // push messages
            userPushMsgService.sendOrCacheMessages(ContentsType.PLAYLIST.getUrl() + ssbPlayListSettings.getId()
                , ssbPlayListSettings.getTitle(), toUser, userPushMessages);

        }
        return new TotalCountRepDto(getTotalLikesCount(ssbPlayListSettings.getToken()));
    }


    @Transactional
    public TotalCountRepDto cancelLikes(Long id, String token) {
        // track 검색
        SsbPlayListSettings ssbPlayListSettings = plyQueryService.findOne(id, token, Status.ON);
        // 사용자 검색
        User user = userQueryService.findOne();

        plyLikesService.cancelLikes(ssbPlayListSettings, user);

        int totalCount = getTotalLikesCount(ssbPlayListSettings.getToken());

        return new TotalCountRepDto(totalCount);
    }


    /**
     * 리플 등록
     *
     */
    @Transactional
    public void addReply(PlyReplySaveReqDto plyReplySaveReqDto) {
        User fromUser = userQueryService.findOne();
        Long settingsId = plyReplySaveReqDto.getPlyId();
        String settingToken = plyReplySaveReqDto.getPlyToken();

        SsbPlayListSettings ssbPlayListSettings = plyQueryService.findOneJoinUser(settingsId, settingToken, Status.ON);

        User ownerUser = ssbPlayListSettings.getUser();
        // 댓글 작성자와 ssbTrack 작성자가 일치한지 확인
        boolean isOwner = ownerUser.getToken().equals(fromUser.getToken());

        // 비공개인데 댓글을 달경우 isOwner 와 같이 체크 권한 있는지 확인 후 없으면 SsbTrackAccessDeniedException
        if (ssbPlayListSettings.getIsPrivacy() && !isOwner) {
            throw new SsbTrackAccessDeniedException("track.error.forbidden", HttpStatus.FORBIDDEN);
        }
        // ssbTrackReply 생성
        SsbPlyReply ssbPlyReply = SsbPlyReply.create(plyReplySaveReqDto, fromUser, ssbPlayListSettings);

        // 댓글 등록
        plyReplyService.addReply(ssbPlyReply, ssbPlayListSettings);

        Set<String> userTagSet = plyReplySaveReqDto.getUserTagSet();
        // 작성자 태그 Set 에서 삭제
        userTagSet.remove(ownerUser.getUserName());
        // 자기 자신을 태그 한 경우 삭제
        userTagSet.remove(fromUser.getUserName());

        Set<User> users = new HashSet<>();

        // 작성자가 아닌 경우
        if (!isOwner) {
            // 작성자 추가
            users.add(ssbPlayListSettings.getUser());
        }
        users.addAll(userQueryService.findUsersByUserNames(userTagSet, Enabled.ENABLED));

        String contents = ssbPlyReply.getContents();
        String linkUrl = ContentsType.PLAYLIST.getUrl() + settingsId + "/" + ssbPlyReply.getId();
        users.forEach(toUser -> {
            UserPushMessages userPushMessages = UserPushMessages.create(toUser, fromUser, PushMsgType.REPLY,
                ContentsType.PLAYLIST, ssbPlyReply.getId());
            userPushMsgService.addUserPushMsg(userPushMessages);
            userPushMsgService.sendOrCacheMessages(linkUrl, contents, toUser, userPushMessages);
        });
    }

    /**
     * 리플 삭제
     *
     * @param plyReplyRmReqDto
     */
    @Transactional
    public void deleteReply(PlyReplyRmReqDto plyReplyRmReqDto) {
//        SsbTrack ssbTrack = trackQueryService.findOne(trackReplyRmReqDto.getTrackId(),
//            trackReplyRmReqDto.getTrackToken(),
//            Status.ON);
        // 삭제를 요청한 사용자
        User user = userQueryService.findOne();
        // 댓글을 가져올때 대 댓글도 모두 검색 후 반환
        List<SsbPlyReply> ssbTrackReplies = plyReplyService.findListAndSubReplies(plyReplyRmReqDto.getReplyId(),
            plyReplyRmReqDto.getReplyToken());

        SsbPlyReply ssbPlyReply = ssbTrackReplies.stream()
            .filter(reply -> reply.getToken().equals(plyReplyRmReqDto.getReplyToken()))
            .findFirst().orElseThrow(
                IllegalArgumentException::new);

        SsbPlayListSettings ssbPlayListSettings = ssbPlyReply.getSsbPlayListSettings();

        // 요청자 아이디
        long uid = user.getId();
        // 트랙 작성 유저
        long trackOwnerUid = ssbPlayListSettings.getUser().getId();

        // 댓글 작성자 고유 아이디 추출
        long ownerUid = ssbPlyReply.getUser().getId();

        // 댓글 작성자도 아니고 트랙 작성자도 아닐시에
        // 권한 에러
        if (ownerUid != uid && trackOwnerUid != uid) {
            throw new SsbTrackAccessDeniedException("access.error.forbidden");
        }
//        List<SsbTrackReply> replies = trackReplyService.findByParentId(ssbTrackReply.getId());
        // 대댓글이 있을 경우에 전부 삭제
        plyReplyService.removeReplies(ssbTrackReplies, ssbPlayListSettings);
    }


    /**
     * playList 좋아요 취소 후 총 좋아요 수 반환
     */

    public int getTotalLikesCount(String token) {
        return plyLikesService.getTotalCount(token);
    }
}

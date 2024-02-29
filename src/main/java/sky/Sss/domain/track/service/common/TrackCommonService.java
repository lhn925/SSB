package sky.Sss.domain.track.service.common;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.common.ReplyRmInfoDto;
import sky.Sss.domain.track.dto.common.ReplySaveReqDto;
import sky.Sss.domain.track.dto.playlist.reply.PlyReplySaveReqDto;
import sky.Sss.domain.track.dto.track.reply.ReplyRmReqDto;
import sky.Sss.domain.track.dto.track.reply.TrackReplySaveReqDto;
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;
import sky.Sss.domain.track.entity.playList.reply.SsbPlyReply;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.reply.SsbTrackReply;
import sky.Sss.domain.track.exception.checked.SsbTrackAccessDeniedException;
import sky.Sss.domain.track.service.playList.PlyQueryService;
import sky.Sss.domain.track.service.playList.reply.PlyReplyService;
import sky.Sss.domain.track.service.track.TrackQueryService;
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
 * 트랙과 플레이리스트에 공통적인 기능을 담당할 서비스
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TrackCommonService {


    private final UserQueryService userQueryService;
    private final TrackQueryService trackQueryService;
    private final PlyQueryService plyQueryService;
    private final PlyReplyService plyReplyService;
    private final TrackReplyService trackReplyService;
    private final UserPushMsgService userPushMsgService;
    // reply


    @Transactional
    public void addReply(ReplySaveReqDto replySaveReqDto, ContentsType contentsType) {
        User user = userQueryService.findOne();
        // 댓글 달 track or playList id
        Long id = replySaveReqDto.getId();
        // 댓글 달 track or playList token
        String token = replySaveReqDto.getToken();

        User ownerUser = null;
        long replyId = 0;

        boolean isOwner = false;
        // 비공개인데 댓글을 달경우 isOwner 와 같이 체크 권한 있는지 확인 후 없으면 SsbTrackAccessDeniedException

        // track reply
        if (ContentsType.TRACK.equals(contentsType)) {
            SsbTrack ssbTrack = trackQueryService.findOneJoinUser(id, token, Status.ON);
            ownerUser = ssbTrack.getUser();
            // 작성자 인지 확인
            isOwner = ownerUser.getToken().equals(user.getToken());
            // 비공개 여부 체크
            checkPrivacy(ssbTrack.getIsPrivacy(), isOwner);
            // ssbTrackReply 생성
            SsbTrackReply ssbTrackReply = SsbTrackReply.create((TrackReplySaveReqDto) replySaveReqDto, user, ssbTrack);
            trackReplyService.addReply(ssbTrackReply, ssbTrack);
            replyId = ssbTrackReply.getId();
        } else { // playList reply
            SsbPlayListSettings ssbPlayListSettings = plyQueryService.findOneJoinUser(id, token, Status.ON);
            ownerUser = ssbPlayListSettings.getUser();
            // 작성자 여부 값
            isOwner = ownerUser.getToken().equals(user.getToken());
            // 비공개 여부 체크
            checkPrivacy(ssbPlayListSettings.getIsPrivacy(), isOwner);
            // ssbTrackReply 생성
            SsbPlyReply ssbPlyReply = SsbPlyReply.create((PlyReplySaveReqDto) replySaveReqDto, user,
                ssbPlayListSettings);
            plyReplyService.addReply(ssbPlyReply, ssbPlayListSettings);
            replyId = ssbPlyReply.getId();
        }

        String linkUrl = contentsType.getUrl() + id + "/" + replyId;
        sendPushToUserSet(replySaveReqDto.getUserTagSet(), replySaveReqDto.getContents(), PushMsgType.REPLY,
            contentsType, user, linkUrl, ownerUser, replyId, isOwner);
    }

    /**
     * 리플 삭제
     *
     * @param replyRmReqDto
     */
    @Transactional
    public void deleteReply(ReplyRmReqDto replyRmReqDto, ContentsType contentsType) {
        // 삭제를 요청한 사용자
        User user = userQueryService.findOne();

        // 댓글을 가져올때 대 댓글도 모두 검색 후 반환
        List<ReplyRmInfoDto> replyDtoList = getReplyRmInfoDtoList(replyRmReqDto.getReplyId(),
            replyRmReqDto.getReplyToken(), contentsType);

        ReplyRmInfoDto replyDto = replyDtoList.stream()
            .filter(reply -> reply.getReplyToken().equals(replyRmReqDto.getReplyToken()))
            .findFirst().orElseThrow(
                IllegalArgumentException::new);

        String token = replyDto.getTrackToken();

        // 요청자 아이디
        long uid = user.getId();
        // 트랙 작성 유저
        long trackOwnerUid = replyDto.getTrackOwner();

        // 댓글 작성자 고유 아이디 추출
        long ownerUid = replyDto.getReplyOwner();

        // 댓글 작성자도 아니고 트랙 작성자도 아닐시에
        // 권한 에러
        if (ownerUid != uid && trackOwnerUid != uid) {
            throw new SsbTrackAccessDeniedException("access.error.forbidden");
        }
        removeReplies(replyDtoList, token, contentsType);
    }

    private void removeReplies(List<ReplyRmInfoDto> replyDtoList, String token, ContentsType contentsType) {
        if (contentsType.equals(ContentsType.TRACK)) {
            trackReplyService.removeReplies(replyDtoList, token);
        } else {
            plyReplyService.removeReplies(replyDtoList, token);
        }

    }

    private List<ReplyRmInfoDto> getReplyRmInfoDtoList(Long replyId, String replyToken, ContentsType contentsType) {
        if (contentsType.equals(ContentsType.TRACK)) {
            return trackReplyService.getReplyRmInfoDtoList(replyId, replyToken);
        }
        return plyReplyService.getReplyRmInfoDtoList(replyId, replyToken);
    }


    public void sendPushToUserSet(Set<String> userTagSet, String contents, PushMsgType pushMsgType,
        ContentsType contentsType,
        User user, String linkUrl, User ownerUser, long contentsId, boolean isOwner) {
        // 작성자 태그 Set 에서 삭제
        userTagSet.remove(ownerUser.getUserName());
        // 자기 자신을 태그 한 경우 삭제
        userTagSet.remove(user.getUserName());
        Set<User> users = new HashSet<>();

        // 작성자가 아닌 경우
        if (!isOwner) {
            // 댓글 작성자 추가
            users.add(ownerUser);
        }
        users.addAll(userQueryService.findUsersByUserNames(userTagSet, Enabled.ENABLED));

        users.forEach(toUser -> {
            UserPushMessages userPushMessages =
                UserPushMessages.create(toUser, user, pushMsgType, contentsType, contentsId);
            userPushMsgService.addUserPushMsg(userPushMessages);
            userPushMsgService.sendOrCacheMessages(linkUrl, contents, toUser, userPushMessages);
        });
    }

    private static void checkPrivacy(boolean isPrivacy, boolean isOwner) {
        if (isPrivacy && !isOwner) {
            throw new SsbTrackAccessDeniedException("track.error.forbidden", HttpStatus.FORBIDDEN);
        }
    }

    // like


}

package sky.Sss.domain.track.service.common;


import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.common.LikeTargetInfoDto;
import sky.Sss.domain.track.dto.track.TotalCountRepDto;
import sky.Sss.domain.track.service.playList.PlyLikesService;
import sky.Sss.domain.track.service.playList.PlyQueryService;
import sky.Sss.domain.track.service.playList.reply.PlyReplyLikesService;
import sky.Sss.domain.track.service.playList.reply.PlyReplyService;
import sky.Sss.domain.track.service.track.TrackLikesService;
import sky.Sss.domain.track.service.track.TrackQueryService;
import sky.Sss.domain.track.service.track.reply.TrackReplyLikesService;
import sky.Sss.domain.track.service.track.reply.TrackReplyService;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.UserPushMessages;
import sky.Sss.domain.user.model.ContentsType;
import sky.Sss.domain.user.model.PushMsgType;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.domain.user.service.push.UserPushMsgService;
import sky.Sss.global.redis.service.RedisCacheService;


/**
 * 트랙과 플레이리스트에 공통적인 기능을 담당할 서비스
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikesCommonService {


    private final UserQueryService userQueryService;
    private final TrackQueryService trackQueryService;
    private final PlyQueryService plyQueryService;

    private final PlyReplyService plyReplyService;
    private final TrackReplyService trackReplyService;

    private final TrackLikesService trackLikesService;
    private final PlyLikesService plyLikesService;
    private final TrackReplyLikesService trackReplyLikesService;
    private final PlyReplyLikesService plyReplyLikesService;
    // reply

    private final UserPushMsgService userPushMsgService;

    private final RedisCacheService redisCacheService;

    /**
     * 좋아요 추가
     *
     * @param id
     * @param token
     * @param contentsType
     * @return
     */
    @Transactional
    public TotalCountRepDto addLikes(Long id, String token, ContentsType contentsType) {
        // track 검색
        LikeTargetInfoDto likeTargetInfoDto = getLikeTargetInfoDto(id, token, contentsType);
        // 사용자 검색
        User fromUser = userQueryService.findOne();
        // push 를 받을 사용자
        User toUser = likeTargetInfoDto.getToUser();

        long targetId = likeTargetInfoDto.getTargetId();
        String targetToken = likeTargetInfoDto.getTargetToken();

        // like 추가
        addLikeAndType(contentsType, fromUser, targetId, targetToken);

        // 총 likes count
        int totalLikesCount = getTotalCount(targetToken, contentsType);

        // userPushMessages 객체 생성
        UserPushMessages userPushMessages = UserPushMessages.create(toUser, fromUser, PushMsgType.LIKES,
            contentsType, targetId);

        StringBuilder linkUrl = new StringBuilder(contentsType.getUrl());

        // 리플 like 인 경우 url 에 parentId(trackId,playList) 추가
        if (likeTargetInfoDto.getParentId() != 0) {
            linkUrl.append(likeTargetInfoDto.getParentId()).append("/");
        }
        linkUrl.append(targetId);

        try {
            // 같은 사용자 인지 확인
            if (!fromUser.getToken().equals(toUser.getToken())) {
                // userPushMessages Table insert
                userPushMsgService.addUserPushMsg(userPushMessages);
                // push messages
                userPushMsgService.sendOrCacheMessages(linkUrl.toString(),
                    likeTargetInfoDto.getTargetContents(),
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
    public TotalCountRepDto cancelLikes(Long targetId, String targetToken, ContentsType contentsType) {
        // 사용자 검색
        User user = userQueryService.findOne();
        // track 검색
        LikeTargetInfoDto targetInfoDto = getLikeTargetInfoDto(targetId, targetToken, contentsType);

        cancelLikeAndType(targetInfoDto.getTargetId(), targetInfoDto.getTargetToken(), user, contentsType);

        int totalCount = getTotalCount(targetToken, contentsType);

        return new TotalCountRepDto(totalCount);
    }

    private void cancelLikeAndType(long targetId, String targetToken, User user, ContentsType contentsType) {
        if (contentsType.equals(ContentsType.TRACK)) {
            trackLikesService.cancelLikes(targetId, targetToken, user);
        } else if (contentsType.equals(ContentsType.PLAYLIST)) {
            plyLikesService.cancelLikes(targetId, targetToken, user);
        } else if (contentsType.equals(ContentsType.REPLY_TRACK)) {
            trackReplyLikesService.cancelLikes(targetId, targetToken, user);
        } else {
            plyReplyLikesService.cancelLikes(targetId, targetToken, user);
        }
    }


    private void addLikeAndType(ContentsType contentsType, User fromUser, long targetId, String targetToken) {
        if (contentsType.equals(ContentsType.TRACK)) {
            trackLikesService.addLikes(targetId, targetToken, fromUser);
        } else if (contentsType.equals(ContentsType.PLAYLIST)) {
            plyLikesService.addLikes(targetId, targetToken, fromUser);
        } else if (contentsType.equals(ContentsType.REPLY_TRACK)) {
            trackReplyLikesService.addLikes(targetId, targetToken, fromUser);
        } else {
            plyReplyLikesService.addLikes(targetId, targetToken, fromUser);
        }
    }

    private LikeTargetInfoDto getLikeTargetInfoDto(Long targetId, String targetToken, ContentsType contentsType) {
        if (contentsType.equals(ContentsType.TRACK)) {
            return trackQueryService.getLikeTargetInfoDto(targetId, targetToken, Status.ON);
        } else if (contentsType.equals(ContentsType.PLAYLIST)) {
            return plyQueryService.getLikeTargetInfoDto(targetId, targetToken, Status.ON);
        } else if (contentsType.equals(ContentsType.REPLY_TRACK)) {
            return trackReplyService.getLikeTargetInfoDto(targetId, targetToken);
        } else {
            return plyReplyService.getLikeTargetInfoDto(targetId, targetToken);
        }
    }

    private List<User> getUserList(String targetToken, ContentsType contentsType) {
        List<User> users = null;
        switch (contentsType) {
            case TRACK -> users = trackLikesService.getUserList(targetToken);
            case PLAYLIST -> users = plyLikesService.getUserList(targetToken);
            case REPLY_TRACK -> users = trackReplyLikesService.getUserList(targetToken);
            case REPLY_PLAYLIST -> users = plyReplyLikesService.getUserList(targetToken);
        }
        return users;
    }

    // likes Total 레디스에서 검색 후 존재하지 않으면 DB 검색 후 반환 검색
    public int getTotalCount(String targetToken,ContentsType contentsType) {
        String key = contentsType.getLikeKeyByType() + targetToken;
        // redis 에 total 캐시가 있으면
        int count = redisCacheService.getTotalCountByKey(new HashMap<>(), key);

        // redis 에 저장이 안되어 있을경우 count 후 저장
        if (count == 0) {
            List<User> users = getUserList(targetToken,contentsType);
            if (!users.isEmpty()) {
                count = users.size();
                redisCacheService.updateCacheMapValueByKey(key, users);
            }
        }
        return count;
    }


}

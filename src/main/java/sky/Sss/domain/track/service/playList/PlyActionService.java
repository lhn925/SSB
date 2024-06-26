package sky.Sss.domain.track.service.playList;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.user.service.UserQueryService;


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
    private final PlyQueryService plyQueryService;


/*
    */
/**
     * playList 좋아요 추가 후 총 좋아요 수 반환
     *//*

    @Transactional
    public TotalCountRepDto addLikes(Long id, String token) {
        // track 검색
        SsbPlayListSettings ssbPlayListSettings = plyQueryService.findOneJoinUser(id, token, Status.ON);

        // 사용자 검색
        User fromUser = userQueryService.findOneByTrackId();

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
*/

/*

    @Transactional
    public TotalCountRepDto cancelLikes(Long id, String token) {
        // track 검색
        SsbPlayListSettings ssbPlayListSettings = plyQueryService.findOneByTrackId(id, token, Status.ON);
        // 사용자 검색
        User user = userQueryService.findOneByTrackId();

        plyLikesService.cancelLikes(ssbPlayListSettings, user);

        int totalCount = getTotalLikesCount(ssbPlayListSettings.getToken());

        return new TotalCountRepDto(totalCount);
    }
*/


    /**
     * playList 좋아요 취소 후 총 좋아요 수 반환
     */

    public int getTotalLikesCount(String token) {
        return plyLikesService.getTotalCount(token);
    }
}

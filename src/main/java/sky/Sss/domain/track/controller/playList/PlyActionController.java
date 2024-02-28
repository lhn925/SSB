package sky.Sss.domain.track.controller.playList;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.track.dto.track.TotalCountRepDto;
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;
import sky.Sss.domain.track.service.playList.PlyActionService;
import sky.Sss.domain.track.service.playList.PlyQueryService;
import sky.Sss.domain.user.annotation.UserAuthorize;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.UserPushMessages;
import sky.Sss.domain.user.model.ContentsType;
import sky.Sss.domain.user.model.PushMsgType;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.push.UserPushMsgService;
import sky.Sss.domain.user.service.UserQueryService;


/**
 * 트랙과 관련 사용자 활동을 모아놓은 Controller
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/tracks/ply/action")
@UserAuthorize
@RestController
public class PlyActionController {

    private final PlyActionService plyActionService;
    private final PlyQueryService plyQueryService;
    private final UserQueryService userQueryService;
    private final UserPushMsgService userPushMsgService;

    /**
     * playList 좋아요 등록 후 총 좋아요수 반환
     *
     * @param id
     *     trackId
     * @return
     */
    @PostMapping("/likes/{id}")
    public ResponseEntity<TotalCountRepDto> saveLikes(@PathVariable Long id) {
        if (id == null || id == 0) {
            throw new IllegalArgumentException();
        }
        // track 검색
        SsbPlayListSettings ssbPlayListSettings = plyQueryService.findOneJoinUser(id, Status.ON);

        // 사용자 검색
        User fromUser = userQueryService.findOne();

        User toUser = ssbPlayListSettings.getUser();

        plyActionService.addLikes(ssbPlayListSettings, fromUser);

        UserPushMessages userPushMessages = UserPushMessages.create(toUser, fromUser, PushMsgType.LIKES,
            ContentsType.PLAYLIST,
            ssbPlayListSettings.getId());
        // 현재 유저가 접속 되어 있는지 확인


        // 같은 사용자인지 확인
        if (!fromUser.getToken().equals(toUser.getToken())) {
            userPushMsgService.addUserPushMsg(userPushMessages);
            // push messages
            userPushMsgService.sendOrCacheMessages(ContentsType.PLAYLIST.getUrl() + ssbPlayListSettings.getId()
                , ssbPlayListSettings.getTitle(), toUser, userPushMessages);

        }

        int totalLikesCount = plyActionService.getTotalLikesCount(ssbPlayListSettings.getToken());

        return ResponseEntity.ok(new TotalCountRepDto(totalLikesCount));
    }


    /**
     * playList 좋아요 취소 후 총 좋아요수 반환
     *
     * @param id
     *     trackId
     * @return
     */
    @DeleteMapping("/likes/{id}")
    public ResponseEntity<TotalCountRepDto> removeLikes(@PathVariable Long id) {
        if (id == null || id == 0) {
            throw new IllegalArgumentException();
        }
        // track 검색
        SsbPlayListSettings ssbPlayListSettings = plyQueryService.findById(id, Status.ON);
        plyActionService.cancelLikes(ssbPlayListSettings);

        int totalCount = plyActionService.getTotalLikesCount(ssbPlayListSettings.getToken());

        return ResponseEntity.ok(new TotalCountRepDto(totalCount));
    }

}

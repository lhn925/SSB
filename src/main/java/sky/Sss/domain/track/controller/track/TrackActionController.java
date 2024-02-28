package sky.Sss.domain.track.controller.track;


import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.track.dto.track.TotalCountDto;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.service.track.TrackActionService;
import sky.Sss.domain.track.service.track.TrackQueryService;
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
@RequestMapping("/tracks/action")
@UserAuthorize
@RestController
public class TrackActionController {

    private final TrackActionService trackActionService;
    /**
     * track 좋아요 등록
     */
    /**
     * @param id
     *     trackId
     * @return
     */
    @PostMapping("/likes/{id}")
    public ResponseEntity<TotalCountDto> saveLikes(@PathVariable Long id) {
        if (id == null || id == 0) {
            throw new IllegalArgumentException();
        }
        return ResponseEntity.ok(trackActionService.addLikes(id));
    }

    /**
     * track 좋아요 취소 후 총 좋아요수 반환
     *
     * @param id
     *     trackId
     * @return
     */
    @DeleteMapping("/likes/{id}")
    public ResponseEntity<TotalCountDto> removeLikes(@PathVariable Long id) {
        if (id == null || id == 0) {
            throw new IllegalArgumentException();
        }

        return ResponseEntity.ok(trackActionService.cancelLike(id));
    }



    /**
     *
     * 좋아요 시 알림
     *
     *
     */

}

package sky.Sss.domain.track.controller.track;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.track.dto.track.TotalLikesCountDto;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.service.track.TrackActionService;
import sky.Sss.domain.track.service.track.TrackQueryService;
import sky.Sss.domain.user.annotation.UserAuthorize;
import sky.Sss.domain.user.model.Status;


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
    private final TrackQueryService trackQueryService;

    /**
     * track 좋아요 등록
     */
    /**
     *
     * @param id trackId
     * @return
     */
    @PostMapping("/likes/{id}")
    public ResponseEntity<TotalLikesCountDto> saveLikes(@PathVariable Long id) {
        if (id == null || id == 0) {
            throw new IllegalArgumentException();
        }
        // track 검색
        SsbTrack ssbTrack = trackQueryService.findById(id, Status.ON);

        trackActionService.addLikes(ssbTrack);

        int totalLikesCount = trackActionService.getTotalLikesCount(ssbTrack.getToken());

        return ResponseEntity.ok(new TotalLikesCountDto(totalLikesCount));
    }
    /**
     * track 좋아요 취소 후 총 좋아요수 반환
     *
     * @param id trackId
     * @return
     */
    @DeleteMapping("/likes/{id}")
    public ResponseEntity<TotalLikesCountDto> removeLikes(@PathVariable Long id) {
        if (id == null || id == 0) {
            throw new IllegalArgumentException();
        }
        // track 검색
        SsbTrack ssbTrack = trackQueryService.findById(id, Status.ON);
        trackActionService.cancelLikes(ssbTrack);

        int totalCount = trackActionService.getTotalLikesCount(ssbTrack.getToken());

        return ResponseEntity.ok(new TotalLikesCountDto(totalCount));
    }

    /**
     *
     * 좋아요 시 알림
     *
     *
     */

}

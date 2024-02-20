package sky.Sss.domain.track.controller.playList;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.track.dto.track.TotalLikesCountDto;
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.service.playList.PlyActionService;
import sky.Sss.domain.track.service.playList.PlyQueryService;
import sky.Sss.domain.user.annotation.UserAuthorize;
import sky.Sss.domain.user.model.Status;


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

    /**
     * playList 좋아요 등록 후 총 좋아요수 반환
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
        SsbPlayListSettings ssbPlayListSettings = plyQueryService.findById(id, Status.ON);

        plyActionService.addLikes(ssbPlayListSettings);

        int totalLikesCount = plyActionService.getTotalLikesCount(ssbPlayListSettings.getToken());

        return ResponseEntity.ok(new TotalLikesCountDto(totalLikesCount));
    }
    /**
     * playList 좋아요 취소 후 총 좋아요수 반환
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
        SsbPlayListSettings ssbPlayListSettings = plyQueryService.findById(id, Status.ON);
        plyActionService.cancelLikes(ssbPlayListSettings);

        int totalCount = plyActionService.getTotalLikesCount(ssbPlayListSettings.getToken());

        return ResponseEntity.ok(new TotalLikesCountDto(totalCount));
    }

}

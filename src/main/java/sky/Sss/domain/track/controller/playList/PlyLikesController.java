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
import sky.Sss.domain.track.service.common.LikesCommonService;
import sky.Sss.domain.user.annotation.UserAuthorize;
import sky.Sss.domain.user.model.ContentsType;


/**
 * 트랙과 관련 사용자 활동을 모아놓은 Controller
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/tracks/ply/likes")
@UserAuthorize
@RestController
public class PlyLikesController {

    private final LikesCommonService likesCommonService;

    /**
     * playList 좋아요 등록 후 총 좋아요수 반환
     *
     * @param id
     * @return
     */
    @PostMapping("/{id}/{token}")
    public ResponseEntity<TotalCountRepDto> saveLikes(@PathVariable Long id, @PathVariable String token) {
        if (id == null || id == 0 || token == null || token.length() == 0) {
            throw new IllegalArgumentException();
        }
        return ResponseEntity.ok(likesCommonService.addLikes(id, token, ContentsType.PLAYLIST));
    }


    /**
     * playList 좋아요 취소 후 총 좋아요수 반환
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<TotalCountRepDto> removeLikes(@PathVariable Long id, @PathVariable String token) {
        if (id == null || id == 0 || token == null || token.length() == 0) {
            throw new IllegalArgumentException();
        }
        return ResponseEntity.ok(likesCommonService.cancelLikes(id, token, ContentsType.PLAYLIST));
    }

    /**
     * @param id
     *     trackId
     */
    @PostMapping("/reply/{id}/{token}")
    public ResponseEntity<TotalCountRepDto> saveReplyLikes(@PathVariable Long id, @PathVariable String token) {
        if (id == null || id == 0 || token == null || token.length() == 0) {
            throw new IllegalArgumentException();
        }
        return ResponseEntity.ok(likesCommonService.addLikes(id, token, ContentsType.REPLY_PLAYLIST));
    }
    /**
     * 리플 좋아요 취소 후 총 좋아요수 반환
     *
     * @param id
     *     trackId
     */
    @DeleteMapping("/reply/{id}/{token}")
    public ResponseEntity<TotalCountRepDto> removeReplyLikes(@PathVariable Long id, @PathVariable String token) {
        if (id == null || id == 0 || token == null || token.length() == 0) {
            throw new IllegalArgumentException();
        }
        return ResponseEntity.ok(likesCommonService.cancelLikes(id, token, ContentsType.REPLY_PLAYLIST));
    }


}

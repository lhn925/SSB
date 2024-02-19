package sky.Sss.domain.track.controller.track;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.track.service.track.TrackActionService;
import sky.Sss.domain.user.annotation.UserAuthorize;
import sky.Sss.domain.user.service.UserQueryService;


/**
 * 트랙과 관련 사용자 활동을 모아놓은 Controller
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping
@RestController("/tracks/action")
@UserAuthorize
public class TrackActionController {


    private final TrackActionService trackActionService;
    private final UserQueryService userQueryService;

    /**
     * track 좋아요 등록
     */
    @PostMapping("/like/{id}")
    public ResponseEntity<?> saveLike(@PathVariable() Long id) {
        if (id == null || id == 0) {
            throw new IllegalArgumentException();
        }

        return null;
    }
    /**
     * 좋아요 취소
     */


}

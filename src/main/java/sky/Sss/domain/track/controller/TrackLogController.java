package sky.Sss.domain.track.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.track.dto.track.log.TrackChartSaveReqDto;
import sky.Sss.domain.track.dto.track.log.TrackPlayLogModifyReqDto;
import sky.Sss.domain.track.service.track.TrackPlaybackMetricsService;
import sky.Sss.domain.user.annotation.UserAuthorize;

/**
 * 트랙에 조회수 관련 api 호출 controller
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/track/log")
@UserAuthorize
public class TrackLogController {
    private final TrackPlaybackMetricsService trackPlaybackMetricsService;
    // 조회수

    // 정지

    // 유저 track 상태 업데이트

    // 공식플레이 집계
    /**
     *
     * chart 에 반영되는 track 플레이 로그 값을 확인하고
     * chart 에 반영
     *
     * @return
     */
    @PostMapping("/chart")
    public ResponseEntity<HttpStatus> saveChartLog (@RequestBody TrackChartSaveReqDto trackChartSaveReqDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        trackPlaybackMetricsService.createChartIncluded(trackChartSaveReqDto);
        return ResponseEntity.ok().build();
    }

    /**
     *
     *
     * playLog 수정 controller
     *
     * @return
     */
    @PutMapping("/chart")
    public ResponseEntity<HttpStatus> modifyChartLog (@RequestBody TrackPlayLogModifyReqDto trackPlayLogModifyReqDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        trackPlaybackMetricsService.modifyPlayLog(trackPlayLogModifyReqDto);
        return ResponseEntity.ok().build();
    }

}

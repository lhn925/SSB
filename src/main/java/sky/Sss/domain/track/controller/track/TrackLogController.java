package sky.Sss.domain.track.controller.track;


import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.track.dto.track.chart.TrackChartSaveReqDto;
import sky.Sss.domain.track.dto.track.log.TrackPlayLogModifyReqDto;
import sky.Sss.domain.track.exception.checked.SsbPlayIncompleteException;
import sky.Sss.domain.track.service.track.play.TrackPlayMetricsService;
import sky.Sss.domain.user.annotation.UserAuthorize;

/**
 * 트랙에 조회수 관련 api 호출 controller
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/tracks/log")
@UserAuthorize
public class TrackLogController {
    private final TrackPlayMetricsService trackPlayMetricsService;
    // 조회수

    // 정지

    // 유저 track 상태 업데이트

    // 공식플레이 집계
    /**
     * 사용자가 한시간에 한번씩 차트에 집계되는 플레이로그
     * 처음부터 끝까지 버튼 조작없이 한번에 다 들었을경우 호출
     * chart 에 반영되는 track 플레이 로그 값을 확인하고
     * chart 에 반영
     * @param trackChartSaveReqDto
     * @param bindingResult
     * @return
     */
    @PostMapping("/chart")
    public ResponseEntity<HttpStatus> saveChartLog (@Validated @RequestBody TrackChartSaveReqDto trackChartSaveReqDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException();
        }
        trackPlayMetricsService.addChartIncluded(trackChartSaveReqDto);
        return ResponseEntity.ok().build();
    }
    /**
     *
     * 사용자가 최소 재생시간이 지난 뒤
     * 뒤로감기나 앞으로감기 혹은 정지 버튼을 눌렀을 경우
     * playLog 수정 controller
     *
     * @return
     */
    /**
     * 사용자가 최소 재생시간이 지난 뒤
     * 뒤로감기나 앞으로감기 혹은 정지 버튼을 눌렀을 경우
     * playLog 수정 controller
     * @param trackPlayLogModifyReqDto
     * @param bindingResult
     * @return
     * @throws SsbPlayIncompleteException
     */
    @PutMapping
    public ResponseEntity<?> modifyPlayLog (@Validated @RequestBody TrackPlayLogModifyReqDto trackPlayLogModifyReqDto, BindingResult bindingResult) throws SsbPlayIncompleteException {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException();
        }
        trackPlayMetricsService.modifyPlayLog(trackPlayLogModifyReqDto);
        return ResponseEntity.ok().build();
    }

}

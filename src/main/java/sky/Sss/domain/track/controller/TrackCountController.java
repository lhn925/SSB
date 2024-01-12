package sky.Sss.domain.track.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.track.dto.track.count.TrackCountReqListDto;
import sky.Sss.domain.track.service.track.TrackPlayCountService;
import sky.Sss.global.error.dto.ErrorGlobalResultDto;
import sky.Sss.global.error.dto.Result;

/**
 * 트랙에 조회수 관련 api 호출 controller
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/track/count")
public class TrackCountController {

    private final TrackPlayCountService trackPlayCountService;


    // 조회수

    // 정지

    // 유저 track 상태 업데이트

    /**
     * 트랙 70퍼 이상 재생 && 한시간 한번만 집계
     * 프론트 쪽에서 재생한 시간
     * 혹은 현재 곡이 다시 재생 요청시 closeTime 기록
     * 기존에 있던 기록이 완료가 안되어있을경우 새로 데이터 생성후 기록
     * 사이트를 나갈경우 그대로 종료
     * 한시간에 한번씩 집계 후 랭킹 테이블에 저장
     * 정시에 이전시간 조회수 집계 후 반영
     *
     *
     *
     *
     * 트랙 조회수 업데이트
     * @return
     */

    /**
     * 유저의 track 재생 상태 update
     *
     * 00,15,30,45
     *
     * @return
     */
    @PutMapping("/status")
    public ResponseEntity<?> modifyPlayStatus (@RequestBody TrackCountReqListDto trackCountReqListDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }



        return null;
    }





}

package sky.Sss.domain.track.controller.track;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.track.dto.track.reply.TrackReplyRmReqDto;
import sky.Sss.domain.track.dto.track.reply.TrackReplySaveReqDto;
import sky.Sss.domain.track.service.track.TrackActionService;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/tracks/reply")
public class TrackReplyController {


    private final TrackActionService trackActionService;

    // 해시태그
    // userName 으로 할것인가
    // 등록
    @PostMapping
    public ResponseEntity<?> saveReply(@Validated @RequestBody TrackReplySaveReqDto trackReplySaveReqDto,
        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException();
        }
        trackActionService.addReply(trackReplySaveReqDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    // 삭제

    @DeleteMapping
    public ResponseEntity<?> removeReply(@Validated @RequestBody TrackReplyRmReqDto trackReplyRmReqDto,
        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException();
        }
        trackActionService.deleteReply(trackReplyRmReqDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }



}

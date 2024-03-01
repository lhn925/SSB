package sky.Sss.domain.track.controller.playList;


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
import sky.Sss.domain.track.dto.playlist.reply.PlyReplySaveReqDto;
import sky.Sss.domain.track.dto.track.reply.ReplyRmReqDto;
import sky.Sss.domain.track.service.common.LikesCommonService;
import sky.Sss.domain.track.service.common.ReplyCommonService;
import sky.Sss.domain.user.model.ContentsType;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/tracks/ply/reply")
public class PlyReplyController {


    private final ReplyCommonService replyCommonService;

    // 해시태그
    // userName 으로 할것인가
    // 등록
    @PostMapping
    public ResponseEntity<?> saveReply(@Validated @RequestBody PlyReplySaveReqDto plyReplySaveReqDto,
        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException();
        }
        replyCommonService.addReply(plyReplySaveReqDto, ContentsType.PLAYLIST);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    // 삭제
    @DeleteMapping
    public ResponseEntity<?> removeReply(@Validated @RequestBody ReplyRmReqDto replyRmReqDto,
        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException();
        }
        replyCommonService.deleteReply(replyRmReqDto,ContentsType.PLAYLIST);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

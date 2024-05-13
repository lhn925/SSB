package sky.Sss.domain.track.controller.common;


import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.track.dto.common.RepostModifyReqDto;
import sky.Sss.domain.track.dto.common.RepostRmReqDto;
import sky.Sss.domain.track.dto.common.RepostSaveReqDto;
import sky.Sss.domain.track.dto.track.rep.TotalCountRepDto;
import sky.Sss.domain.track.service.common.RepostCommonService;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/tracks/repost")
public class RepostController {

    private final RepostCommonService repostCommonService;


    /**
     * Repost 등록 (트랙,플레이리스트)
     *
     * @param repostSaveReqDto
     * @param bindingResult
     * @return
     */
    @PostMapping
    public ResponseEntity<TotalCountRepDto> saveRepost(@Validated @RequestBody RepostSaveReqDto repostSaveReqDto,
        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException();
        }

        repostCommonService.addRepost(repostSaveReqDto);
        // repost 통합 갯수 전달
        return ResponseEntity.ok(new TotalCountRepDto(
            repostCommonService.getTotalCount(repostSaveReqDto.getTargetId(), repostSaveReqDto.getTargetToken(),
                repostSaveReqDto.getContentsType())));
    }

    /**
     * 코멘트 수정 및 등록
     */
    @PutMapping("/comment")
    public ResponseEntity<?> modifyComment(@Validated @RequestBody RepostModifyReqDto repostModifyReqDto,
        BindingResult bindingResult) throws IOException {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException();
        }
        repostCommonService.updateComment(repostModifyReqDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * Repost 삭제
     *
     * @param bindingResult
     * @return
     */
    @DeleteMapping
    public ResponseEntity<TotalCountRepDto> removeRepost(@Validated @RequestBody RepostRmReqDto repostRmReqDto,
        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException();
        }
        int totalCount = repostCommonService.deleteRepost(repostRmReqDto);
        return ResponseEntity.ok(new TotalCountRepDto(totalCount));
    }

}

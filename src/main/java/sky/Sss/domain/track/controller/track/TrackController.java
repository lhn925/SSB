package sky.Sss.domain.track.controller.track;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sky.Sss.domain.track.dto.track.TrackRmReqDto;
import sky.Sss.domain.track.dto.track.TrackInfoRepDto;
import sky.Sss.domain.track.dto.track.TrackInfoSaveReqDto;
import sky.Sss.domain.track.dto.track.TrackInfoModifyReqDto;
import sky.Sss.domain.track.service.track.TrackService;
import sky.Sss.domain.user.annotation.UserAuthorize;
import sky.Sss.global.error.dto.ErrorResultDto;
import sky.Sss.global.error.dto.Result;

/**
 *
 *
 * 트랙 생성,수정,삭제 Crud를 모아 놓은 Controller
 *
 *
 *
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/tracks")
@RestController
@UserAuthorize
public class TrackController {

    private final TrackService trackService;
    private final MessageSource ms;
    /**
     * track 생성
     * track 수정
     * track 삭제
     *
     *
     * @param trackMetaUploadDto
     * @param bindingResult
     * @param request
     * @return
     */
    /**
     * 개별곡 저장
     */
    @PostMapping
    public ResponseEntity<?> saveTrack(@Validated @RequestPart TrackInfoSaveReqDto trackInfoSaveReqDto,
        BindingResult bindingResult,
        @RequestPart(required = false) MultipartFile coverImgFile,
        HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }

        HttpSession session = request.getSession();
        TrackInfoRepDto trackInfoRepDto = trackService.addTrackFile(trackInfoSaveReqDto, coverImgFile, session.getId());
        return ResponseEntity.ok(trackInfoRepDto);
    }

    /**
     * 트랙정보 업데이트
     *
     * @return
     */
    @PutMapping
    public ResponseEntity<?> modifyTrack(@Validated @RequestPart TrackInfoModifyReqDto trackInfoModifyReqDto,
        @RequestPart(required = false) MultipartFile coverImgFile,
        BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        trackService.updateTrackInfo(trackInfoModifyReqDto, coverImgFile);
        return ResponseEntity.ok().build();
    }

    /**
     * 트랙 파일 삭제
     */
    @DeleteMapping
    public ResponseEntity<?> removeTrack(@Validated @RequestBody TrackRmReqDto trackRmReqDto,
        BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        trackService.deleteTrack(trackRmReqDto.getId(), trackRmReqDto.getToken());
        return ResponseEntity.ok().build();
    }
}

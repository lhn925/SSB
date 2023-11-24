package sky.Sss.domain.track.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.track.dto.track.TotalLengthDto;
import sky.Sss.domain.track.dto.track.TrackDeleteDto;
import sky.Sss.domain.track.dto.track.TrackInfoSaveDto;
import sky.Sss.domain.track.dto.track.TrackInfoUpdateDto;
import sky.Sss.domain.track.exception.TrackFileNotFoundException;
import sky.Sss.domain.track.exception.TrackLengthLimitOverException;
import sky.Sss.domain.track.service.TrackService;
import sky.Sss.domain.user.annotation.UserAuthorize;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.global.error.dto.ErrorGlobalResultDto;
import sky.Sss.global.error.dto.ErrorResultDto;
import sky.Sss.global.error.dto.Result;
import sky.Sss.global.file.utili.FileStore;


@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user/track")
@RestController
@UserAuthorize
public class TrackController {


    private final TrackService trackService;
    private final MessageSource ms;
    private final UserQueryService userQueryService;
    /**
     * 플레이 리스트 수정
     * 플레이 리스트 삭제
     * 플레이 리스트 개별 곡 삭제
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
    public ResponseEntity saveTrack(@Validated @ModelAttribute TrackInfoSaveDto trackInfoSaveDto,
        BindingResult bindingResult,
        HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        HttpSession session = request.getSession();
        try {
            trackService.saveTrackFile(trackInfoSaveDto, session.getId());
        } catch (TrackFileNotFoundException e) {
            bindingResult.reject("file.error.notFind");
            return Result.getErrorResult(new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()),
                HttpStatus.NOT_FOUND);
        } catch (TrackLengthLimitOverException e) {
            bindingResult.reject("track.limit.error");
            return Result.getErrorResult(new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        } catch (IOException e) {
            bindingResult.reject("error");
            return Result.getErrorResult(new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }

    /**
     * 트랙정보 업데이트
     *
     * @return
     */
    @PutMapping
    public ResponseEntity updateTrack(@Validated @ModelAttribute TrackInfoUpdateDto trackInfoUpdateDto,
        BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        try {
            trackService.updateTrackInfo(trackInfoUpdateDto);
        } catch (TrackFileNotFoundException e) {
            bindingResult.reject("track.update.error");
            return Result.getErrorResult(new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()),
                HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            bindingResult.reject("error");
            return Result.getErrorResult(new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 트랙 파일 삭제
     */
    @DeleteMapping
    public ResponseEntity deleteTrack(@Validated @RequestBody TrackDeleteDto trackDeleteDto,
        BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        try {
            trackService.deleteTrack(trackDeleteDto.getId(), trackDeleteDto.getToken());
        } catch (TrackFileNotFoundException e) {
            bindingResult.reject("track.delete.error");
            return Result.getErrorResult(new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()),
                HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/total")
    public ResponseEntity getTotalLength() {
        Integer totalLength = trackService.getTotalLength(userQueryService.findOne());
        return new ResponseEntity(new TotalLengthDto(totalLength, FileStore.TRACK_UPLOAD_LIMIT), HttpStatus.OK);
    }


}

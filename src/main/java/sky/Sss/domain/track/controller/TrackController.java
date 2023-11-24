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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.track.dto.temp.TempTrackDeleteDto;
import sky.Sss.domain.track.dto.temp.TempTrackInfoDto;
import sky.Sss.domain.track.dto.track.TrackMetaUploadDto;
import sky.Sss.domain.track.dto.temp.TempTrackFileUploadDto;
import sky.Sss.domain.track.dto.track.TrackPlayListInfoDto;
import sky.Sss.domain.track.dto.track.TrackPlayListSettingDto;
import sky.Sss.domain.track.exception.TrackLengthLimitOverException;
import sky.Sss.domain.track.service.TempTrackStorageService;
import sky.Sss.domain.track.service.TrackService;
import sky.Sss.domain.user.annotation.UserAuthorize;
import sky.Sss.global.error.dto.ErrorGlobalResultDto;
import sky.Sss.global.error.dto.Result;


@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user/track/file")
@RestController
@UserAuthorize
public class TrackFileController {


    private final TrackService trackService;
    private final MessageSource ms;

    @PostMapping
    public ResponseEntity saveTrack(@Validated @RequestBody TrackMetaUploadDto trackMetaUploadDto, BindingResult bindingResult,
        HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        }
        HttpSession session = request.getSession();
        try {
            trackService.saveTrackFile(trackMetaUploadDto, session.getId());
        } catch (TrackLengthLimitOverException e) {
            bindingResult.reject("track.limit.error");
            return Result.getErrorResult(new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        } catch (IOException e) {
            bindingResult.reject("error");
            return Result.getErrorResult(new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }

    @PostMapping("/files")
    public ResponseEntity savePlayList(@Validated @RequestBody TrackPlayListSettingDto trackPlayListSettingDto, BindingResult bindingResult,
        HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        }
        HttpSession session = request.getSession();
        try {
            TrackPlayListInfoDto trackPlayListInfoDto = trackService.saveTrackFiles(trackPlayListSettingDto,session.getId());
            return new ResponseEntity(trackPlayListInfoDto, HttpStatus.OK);
        } catch (TrackLengthLimitOverException e) {
            bindingResult.reject("track.limit.error");
            return Result.getErrorResult(new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        } catch (IOException e) {
            bindingResult.reject("error");
            return Result.getErrorResult(new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

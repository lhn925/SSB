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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.track.dto.playlist.PlayListInfoDto;
import sky.Sss.domain.track.dto.playlist.PlayListSettingDto;
import sky.Sss.domain.track.exception.TrackLengthLimitOverException;
import sky.Sss.domain.track.service.TrackService;
import sky.Sss.domain.user.annotation.UserAuthorize;
import sky.Sss.global.error.dto.ErrorGlobalResultDto;
import sky.Sss.global.error.dto.Result;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user/playlist")
@RestController
@UserAuthorize
public class PlayListController {

    private final TrackService trackService;
    private final MessageSource ms;

    /**
     * 새로 생성
     * 트랙 추가
     * 플레이리스트 삭제
     */

    /**
     * 플레이리스트 생성
     * @param playListSettingDto
     * @param bindingResult
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity savePlayList(@Validated @RequestBody PlayListSettingDto playListSettingDto, BindingResult bindingResult,
        HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        }
        HttpSession session = request.getSession();
        try {
            PlayListInfoDto trackPlayListInfoDto = trackService.saveTrackFiles(playListSettingDto,session.getId());
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

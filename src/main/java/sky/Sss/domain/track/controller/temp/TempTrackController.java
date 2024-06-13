package sky.Sss.domain.track.controller.temp;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.track.dto.temp.req.TempTrackFileUploadDto;
import sky.Sss.domain.track.dto.temp.rep.TempTrackInfoDto;
import sky.Sss.domain.track.dto.temp.req.TempTracksDeleteDto;
import sky.Sss.domain.track.service.temp.TempTrackStorageService;
import sky.Sss.domain.user.annotation.UserAuthorize;
import sky.Sss.global.error.dto.ErrorGlobalResultDto;
import sky.Sss.global.error.dto.Result;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/tracks/temp/file")
@RestController
@UserAuthorize
public class TempTrackController {


    private final TempTrackStorageService tempTrackStorageService;
    private final MessageSource ms;
    /**
     *
     *
     * 수정
     * 삭제
     *
     */
    /*

        json 형식으로 나가지 않는 형상 해결 해야됨
     * 임시파일 저장
     */
    @PostMapping
    public ResponseEntity<?> saveTempTrackFile(@Validated @ModelAttribute TempTrackFileUploadDto tempTrackFileUploadDto,
        BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorGlobalResultDto(bindingResult.getFieldErrors(),ms,request.getLocale()));
        }
        TempTrackInfoDto tempTrackInfoDto = tempTrackStorageService.saveTempTrackFile(tempTrackFileUploadDto);

        return ResponseEntity.ok(tempTrackInfoDto);
    }
    /**
     * 임시파일 all 삭제
     *
     * @param request
     * @return
     */
    @DeleteMapping
    public ResponseEntity<?> cancelTempFile(@Validated @RequestBody TempTracksDeleteDto tempTracksDeleteDto,
        BindingResult bindingResult, HttpServletRequest request) throws IOException {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        }
        tempTrackStorageService.deleteAll(tempTracksDeleteDto.getTempTrackDeleteList());
        return ResponseEntity.ok().build();
    }

}

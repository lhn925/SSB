package sky.Sss.domain.track.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.track.dto.temp.TempTrackDeleteDto;
import sky.Sss.domain.track.dto.temp.TempTrackFileUploadDto;
import sky.Sss.domain.track.dto.temp.TempTrackInfoDto;
import sky.Sss.domain.track.service.temp.TempTrackStorageService;
import sky.Sss.domain.user.annotation.UserAuthorize;
import sky.Sss.global.error.dto.ErrorGlobalResultDto;
import sky.Sss.global.error.dto.Result;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user/temp/file")
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

    /**
     * 임시파일 저장
     *
     * @param tempTrackFileUploadDto
     * @param bindingResult
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity saveTempTrackFile(@Validated @ModelAttribute TempTrackFileUploadDto tempTrackFileUploadDto,
        BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        }
        HttpSession session = request.getSession();
        TempTrackInfoDto tempTrackInfoDto = tempTrackStorageService.saveTempTrackFile(tempTrackFileUploadDto,
            session.getId());
        return ResponseEntity.ok(tempTrackInfoDto);

    }

    /**
     * 임시파일 삭제
     *
     * @param request
     * @return
     */
    @DeleteMapping
    public ResponseEntity removeTempFile(@Validated @ModelAttribute TempTrackDeleteDto tempTrackDeleteDto,
        BindingResult bindingResult, HttpServletRequest request) throws IOException {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        }
        HttpSession session = request.getSession();

        String sessionId = session.getId();

        tempTrackStorageService.deleteAllBatch(tempTrackDeleteDto.getId(), tempTrackDeleteDto.getToken(), sessionId);
        return ResponseEntity.ok().build();
    }

}

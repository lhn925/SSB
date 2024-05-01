package sky.Sss.domain.track.controller.track;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.track.dto.track.TotalLengthRepDto;
import sky.Sss.domain.track.dto.track.TrackInfoReqDto;
import sky.Sss.domain.track.dto.track.TrackInfoSimpleDto;
import sky.Sss.domain.track.dto.track.TrackPlayRepDto;
import sky.Sss.domain.track.dto.track.reply.TracksInfoReqDto;
import sky.Sss.domain.track.exception.checked.SsbTrackAccessDeniedException;
import sky.Sss.domain.track.service.track.TrackService;
import sky.Sss.domain.user.annotation.UserAuthorize;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.global.file.utili.FileStore;

/**
 */

@Slf4j
@RestController
@RequestMapping("/tracks/info")
@RequiredArgsConstructor
public class TrackInfoController {
    private final TrackService trackService;
    private final UserQueryService userQueryService;

    /**
     * TrackInfoSimpleDto 출력
     *
     * @param id track Id
     * @return
     * @throws SsbTrackAccessDeniedException
     */
    @GetMapping("/search/{id}")
    public ResponseEntity<TrackInfoSimpleDto> searchTrackInfo(@PathVariable Long id) throws SsbTrackAccessDeniedException {
        TrackInfoSimpleDto trackInfoSimpleDto = trackService.getTrackInfoSimpleDto(id);
        if (trackInfoSimpleDto == null) {
            throw new SsbTrackAccessDeniedException("track.error.forbidden", HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(trackInfoSimpleDto);
    }


    /**
     *
     * @param tracksInfoReqDto
     * @return
     * @throws SsbTrackAccessDeniedException
     */
    @GetMapping("/search/list")
    public ResponseEntity<List<TrackInfoSimpleDto>> getTrackInfoList(@ModelAttribute TracksInfoReqDto tracksInfoReqDto) throws SsbTrackAccessDeniedException {
        log.info("simpleDtoList = {}","getTrackInfoList");
        List<TrackInfoSimpleDto> simpleDtoList = trackService.getTrackInfoSimpleDtoList(tracksInfoReqDto);



        if (simpleDtoList.isEmpty()) {
            throw new SsbTrackAccessDeniedException("track.error.forbidden", HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(simpleDtoList);
    }

    /**
     * 업로드한 track 시간 총합
     *
     * @return
     */
    @UserAuthorize
    @GetMapping("/total")
    public ResponseEntity<TotalLengthRepDto> getTotalLength() {
        Integer totalLength = trackService.getTotalLength(userQueryService.findOne());
        return ResponseEntity.ok(new TotalLengthRepDto(totalLength, FileStore.TRACK_UPLOAD_LIMIT));
    }
}

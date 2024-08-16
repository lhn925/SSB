package sky.Sss.domain.track.controller.track;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.track.dto.track.rep.TotalLengthRepDto;
import sky.Sss.domain.track.dto.track.common.TrackInfoSimpleDto;
import sky.Sss.domain.track.dto.track.rep.TrackDetailDto;
import sky.Sss.domain.track.dto.track.reply.TracksInfoReqDto;
import sky.Sss.domain.track.exception.checked.SsbTrackAccessDeniedException;
import sky.Sss.domain.track.service.track.TrackService;
import sky.Sss.domain.user.annotation.UserAuthorize;
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
    public ResponseEntity<TrackDetailDto> searchTrackInfo(@PathVariable Long id) throws SsbTrackAccessDeniedException {
        TrackDetailDto trackDetailDto = trackService.getTrackInfoSimpleDto(id);
        if (trackDetailDto == null) {
            throw new SsbTrackAccessDeniedException("track.error.forbidden", HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(trackDetailDto);
    }


    /**
     *
     * @param tracksInfoReqDto
     * @return
     * @throws SsbTrackAccessDeniedException
     */
    @GetMapping("/search/list")
    public ResponseEntity<List<TrackDetailDto>> getTrackInfoList(@ModelAttribute TracksInfoReqDto tracksInfoReqDto) throws SsbTrackAccessDeniedException {
        List<TrackDetailDto> simpleDtoList = trackService.getTrackInfoSimpleDtoList(tracksInfoReqDto);

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
        Integer totalLength = trackService.getTotalLength();
        return ResponseEntity.ok(new TotalLengthRepDto(totalLength, FileStore.TRACK_UPLOAD_LIMIT));
    }
}

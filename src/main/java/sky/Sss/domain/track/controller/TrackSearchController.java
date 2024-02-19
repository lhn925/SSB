package sky.Sss.domain.track.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.track.dto.track.TrackPlayRepDto;
import sky.Sss.domain.track.exception.checked.SsbTrackAccessDeniedException;
import sky.Sss.domain.track.service.track.TrackService;
import sky.Sss.domain.user.model.Status;

/**
 */

@Slf4j
@RestController
@RequestMapping("/users/search/track")
@RequiredArgsConstructor
public class TrackSearchController {
    private final TrackService trackService;

    /**
     * 재생할 트랙 정보 출력
     *
     * @param trackId
     * @param request
     * @return
     * @throws SsbTrackAccessDeniedException
     */
    @GetMapping("/info/{trackId}")
    public ResponseEntity<TrackPlayRepDto> getTrackInfo(@PathVariable Long trackId, HttpServletRequest request) throws SsbTrackAccessDeniedException {
        TrackPlayRepDto trackPlayDto = trackService.authorizedTrackInfo(trackId, Status.ON,
            request.getHeader("User-Agent"));
        if (trackPlayDto == null) {
            throw new SsbTrackAccessDeniedException("track.error.forbidden", HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(trackPlayDto);
    }
}

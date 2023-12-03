package sky.Sss.domain.track.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.track.dto.track.TrackInfoDto;


@RestController
@RequestMapping("/track/query")
public class TrackQueryController {



    @GetMapping("/{trackId}")
    public ResponseEntity<TrackInfoDto> getTrack(@PathVariable String trackId) {

        return null;
    }


}

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


    // 트랙 정보 가져오기
    // 트랙 파일 가져오기
    // 트랙 리스트 가져오기
    //



    @GetMapping("/{trackId}")
    public ResponseEntity getTrack(@PathVariable String trackId) {



        return null;
    }


}

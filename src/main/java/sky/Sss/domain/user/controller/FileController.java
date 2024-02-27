package sky.Sss.domain.user.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.track.service.track.play.TrackPlayService;
import sky.Sss.global.file.utili.FileStore;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/file")
public class FileController {

    private final FileStore fileStore;
    private final TrackPlayService trackPlayService;

    // 굳이 파일을 들고 오는데
    // DB 조회를 해야할까?
    // 물론 정확성은 높이지만 거기에 따른 쿼리는?

    //

    // https://phinf.pstatic.net/contact/20231211_73/1702273532247G5RXT_JPEG/image.jpg?type=s160
    // https://static.nid.naver.com/images/web/users/default.png
    //https://i1.sndcdn.com/avatars-CArjHPU2594ar3d8-zRJ9nw-t500x500.jpg
    //https://i1.sndcdn.com/artworks-ofaAuAuzQo8MEAsM-v2yUgg-t500x500.jpg
    //https://i1.sndcdn.com/artworks-zpso7w5yhucrU6FT-TQdzcg-t500x500.jpg

    /**
     * id:user_file_Api_1
     * <p>
     * 서버에서 이미지 가져오기
     *
     * @return
     * @throws IOException
     */
    @GetMapping("/image/{fileName}")
    public ResponseEntity getUserProfilePicture(@PathVariable String fileName) {
        // file MediaType 확인 후 header 에 저장
        MediaType mediaType = null;
        UrlResource pictureImage = null;
        try {
            if (StringUtils.hasText(fileName)) {
                mediaType = MediaType.parseMediaType(Files.probeContentType(Paths.get(fileName)));
                pictureImage = fileStore.getUrlResource(fileStore.getImageDir() + fileName);
            }
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, mediaType.toString())
            .body(pictureImage);
    }

    /**
     * track 재생
     *
     * @return
     */
    // 한시간에 한번만 플레이 인정
    // track file 출력
    @GetMapping("/track/play/{id}/{token}")
    public ResponseEntity<UrlResource> getTrackFile(@PathVariable Long id, @PathVariable String token) {
        return ResponseEntity.ok(trackPlayService.getTrackPlayFile(id, token));
    }

    @GetMapping("/image/default")
    public ResponseEntity getTrackCoverImg(@PathVariable Long trackId, @PathVariable String userId) {
        return null;
    }

}

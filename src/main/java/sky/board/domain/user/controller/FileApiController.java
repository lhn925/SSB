package sky.board.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.board.domain.user.service.UserQueryService;
import sky.board.domain.user.service.myInfo.UserMyInfoService;
import sky.board.global.file.utili.FileStore;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/file")
public class FileController {
    private final UserMyInfoService userMyInfoService;
    private final UserQueryService userQueryService;

    /**
     * id:myInfo_Api_3
     * <p>
     * 서버에서 프로필 이미지 가져오기
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    @GetMapping("/picture/{fileName}")
    public ResponseEntity getUserProfilePicture(@RequestBody String userId, @PathVariable String fileName)
        throws IOException {


        log.info("userId = {}", userId);
        String token = userQueryService.findOne(userId).getToken();

        // file MediaType 확인 후 header 에 저장
        MediaType mediaType = null;
        UrlResource pictureImage = null;
        mediaType = MediaType.parseMediaType(Files.probeContentType(Paths.get(fileName)));
        log.info("mediaType = {}", mediaType);
        pictureImage = userMyInfoService.getPictureImage(
            FileStore.USER_PICTURE_DIR + token+ "/" + fileName);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, mediaType.toString())
            .body(pictureImage);

    }

}

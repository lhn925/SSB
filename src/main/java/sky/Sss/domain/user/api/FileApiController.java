package sky.Sss.domain.user.api;

import jakarta.servlet.http.HttpServletResponse;
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
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.domain.user.service.myInfo.UserMyInfoService;
import sky.Sss.global.file.utili.FileStore;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/file/api")
public class FileApiController {
    private final UserMyInfoService userMyInfoService;
    private final UserQueryService userQueryService;





    /**
     * id:user_file_Api_1
     * <p>
     * 서버에서 프로필 이미지 가져오기
     *
     * @return
     * @throws IOException
     */
    @GetMapping("/picture/{userId}")
    public ResponseEntity getUserProfilePicture(@PathVariable String userId) {

        // file MediaType 확인 후 header 에 저장

        MediaType mediaType = null;
        UrlResource pictureImage = null;
        try {
            User user = userQueryService.findOne(userId);
            String fileName = user.getPictureUrl();
            if (StringUtils.hasText(fileName)) {
                mediaType = MediaType.parseMediaType(Files.probeContentType(Paths.get(user.getPictureUrl())));
                pictureImage = userMyInfoService.getPictureImage(
                    FileStore.USER_PICTURE_DIR + user.getToken()+ "/" + fileName);
            } else {
                mediaType = MediaType.parseMediaType(
                    Files.probeContentType(Paths.get(FileStore.USER_DEFAULT_IMAGE_URL)));
                pictureImage = userMyInfoService.getPictureImage(FileStore.USER_DEFAULT_IMAGE_URL);
            }
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, mediaType.toString())
            .body(pictureImage);
    }

/*
    @GetMapping("/picture/default")
    public ResponseEntity<Resource> getUserProfilePictureDefault() throws IOException {
        MediaType mediaType = MediaType.parseMediaType(
            Files.probeContentType(Paths.get(FileStore.USER_DEFAULT_IMAGE_URL)));
        UrlResource pictureImage = userMyInfoService.getPictureImage(FileStore.USER_DEFAULT_IMAGE_URL);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, mediaType.toString())
            .body(pictureImage);
    }
*/

}

package sky.board.domain.user.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.board.domain.user.entity.User;
import sky.board.domain.user.exception.DuplicateCheckException;
import sky.board.domain.user.exception.UserInfoNotFoundException;
import sky.board.domain.user.service.UserQueryService;
import sky.board.domain.user.service.myInfo.UserMyInfoService;
import sky.board.global.file.utili.FileStore;

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
    public ResponseEntity getUserProfilePicture(@PathVariable String userId, HttpServletResponse response)
        throws IOException {


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
            response.sendRedirect("/error/404");
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

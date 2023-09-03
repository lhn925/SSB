package sky.board.domain.user.api.myInfo;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.board.domain.user.dto.UserInfoDto;
import sky.board.domain.user.dto.myInfo.UserNameUpdateDto;
import sky.board.domain.user.dto.myInfo.UserPictureUpdateDto;
import sky.board.domain.user.exception.DuplicateCheckException;
import sky.board.domain.user.service.myInfo.UserMyInfoService;
import sky.board.global.error.dto.ErrorGlobalResultDto;
import sky.board.global.error.dto.ErrorResultDto;
import sky.board.global.error.dto.Result;
import sky.board.global.file.dto.UploadFile;
import sky.board.global.file.utili.FileStore;
import sky.board.global.redis.dto.RedisKeyDto;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/myInfo/api")
public class MyInfoApiController {

    private final MessageSource ms;
    private final UserMyInfoService userMyInfoService;


    @PutMapping
    public ResponseEntity putUserName(@Validated @RequestBody UserNameUpdateDto userNameUpdateDto,
        BindingResult bindingResult,
        HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        try {
            // 중복체크
            userMyInfoService.updateUserName(request, userNameUpdateDto);

            return new ResponseEntity(new Result<>(userNameUpdateDto), HttpStatus.OK);
        } catch (DuplicateCheckException e) {
            bindingResult.reject("duplication", new String[]{e.getMessage()}, null);
            return Result.getErrorResult(
                new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        } catch (IllegalArgumentException e) {
            bindingResult.reject("change.isNotAfter", new String[]{e.getMessage()}, null);
            return Result.getErrorResult(
                new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        }
    }


    @PostMapping("/picture")
    public ResponseEntity updateUserProfilePicture(@Validated @ModelAttribute UserPictureUpdateDto file,
        BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        UploadFile uploadFile = null;
        try {
            uploadFile = userMyInfoService.updatePicture(request, file.getFile());
        } catch (FileNotFoundException e) {
            bindingResult.reject(e.getMessage());
            return Result.getErrorResult(
                new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        } catch (IOException e) {
            e.printStackTrace();
            bindingResult.reject("error");
            return Result.getErrorResult(
                new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        }
        return new ResponseEntity(new Result<>(uploadFile), HttpStatus.OK);
    }

    @GetMapping("/picture/{fileName}")
    public ResponseEntity<Resource> getUserProfilePicture(@PathVariable String fileName, HttpServletRequest request)
        throws IOException {

        HttpSession session = request.getSession(false);
        UserInfoDto userInfoDto = (UserInfoDto) session.getAttribute(RedisKeyDto.USER_KEY);

        MediaType mediaType = MediaType.parseMediaType(Files.probeContentType(Paths.get(fileName)));

        UrlResource pictureImage = userMyInfoService.getPictureImage(
            FileStore.USER_PICTURE_DIR + userInfoDto.getToken() + "/" + fileName);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, mediaType.toString())
            .body(pictureImage);
    }

    @GetMapping("/picture/default")
    public ResponseEntity<Resource> getUserProfilePictureDefault() throws IOException {
        MediaType mediaType = MediaType.parseMediaType(
            Files.probeContentType(Paths.get(FileStore.USER_DEFAULT_IMAGE_URL)));
        UrlResource pictureImage = userMyInfoService.getPictureImage(FileStore.USER_DEFAULT_IMAGE_URL);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, mediaType.toString())
            .body(pictureImage);
    }

    @DeleteMapping("/picture")
    public ResponseEntity deleteUserProfilePicture(HttpServletRequest request) {
        try {
            userMyInfoService.deletePicture(request);
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (FileNotFoundException e) {
            return Result.getErrorResult(
                new ErrorGlobalResultDto(e.getMessage(), ms, request.getLocale()));
        } catch (IOException e) {
            return Result.getErrorResult(
                new ErrorGlobalResultDto("error", ms, request.getLocale()));
        }
    }
}

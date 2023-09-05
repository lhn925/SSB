package sky.board.domain.user.api.myInfo;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
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
import sky.board.domain.user.dto.login.CustomUserDetails;
import sky.board.domain.user.dto.myInfo.UserNameUpdateDto;
import sky.board.domain.user.dto.myInfo.UserPictureUpdateDto;
import sky.board.domain.user.dto.myInfo.UserPwUpdateFormDto;
import sky.board.domain.user.exception.DuplicateCheckException;
import sky.board.domain.user.model.ChangeSuccess;
import sky.board.domain.user.model.PwSecLevel;
import sky.board.domain.user.service.UserQueryService;
import sky.board.domain.user.service.help.UserHelpService;
import sky.board.domain.user.service.log.UserActivityLogService;
import sky.board.domain.user.service.login.UserLoginStatusService;
import sky.board.domain.user.service.myInfo.UserMyInfoService;
import sky.board.domain.user.utili.PwChecker;
import sky.board.global.error.dto.ErrorGlobalResultDto;
import sky.board.global.error.dto.ErrorResult;
import sky.board.global.error.dto.ErrorResultDto;
import sky.board.global.error.dto.Result;
import sky.board.global.file.dto.UploadFile;
import sky.board.global.file.utili.FileStore;
import sky.board.global.openapi.service.ApiExamCaptchaNkeyService;
import sky.board.global.redis.dto.RedisKeyDto;
import sky.board.global.utili.Alert;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/myInfo/api")
public class MyInfoApiController {

    private final MessageSource ms;

    private final ApiExamCaptchaNkeyService apiExamCaptchaNkeyService;


    private final UserMyInfoService userMyInfoService;
    private final UserActivityLogService userActivityLogService;
    private final UserHelpService userHelpService;
    private final UserLoginStatusService userLoginStatusService;
    private final UserQueryService userQueryService;

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
        } catch (IllegalArgumentException e) {
            bindingResult.reject(e.getMessage());
            return Result.getErrorResult(
                new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        } catch (DuplicateCheckException e) {
            bindingResult.reject("duplication", new String[]{e.getMessage()}, null);
            return Result.getErrorResult(
                new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        } catch (RuntimeException e) {
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
        } catch (Exception e) {
            bindingResult.reject(e.getMessage());
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
        } catch (RuntimeException e) {
            return Result.getErrorResult(
                new ErrorGlobalResultDto("error", ms, request.getLocale()));
        } catch (FileNotFoundException e) {
            return Result.getErrorResult(
                new ErrorGlobalResultDto(e.getMessage(), ms, request.getLocale()));
        }
    }


    @PostMapping("/pw/update")
    public ResponseEntity updateUserPassWord(@Validated @RequestBody UserPwUpdateFormDto userPwUpdateFormDto,
        BindingResult bindingResult, HttpServletRequest request) throws IOException {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }

        boolean isCaptcha;
        Map result = apiExamCaptchaNkeyService.getApiExamCaptchaNkeyResult(
            userPwUpdateFormDto.getCaptchaKey(), userPwUpdateFormDto.getCaptcha());

        isCaptcha = (boolean) result.get("result");

        // 자동입력 방지 번호가 맞지 않은 경우
        if (!isCaptcha) {
            return getErrorResultResponseEntity(bindingResult, "error.captcha", userPwUpdateFormDto, request);

        }

        // 확인 비밀번호가 불일치 할 경우
        if (!userPwUpdateFormDto.getUpdatePw().equals(userPwUpdateFormDto.getUpdatePwChk())) {
            return getErrorResultResponseEntity(bindingResult, "pw.mismatch", userPwUpdateFormDto, request);
        }
        // 비밀번호 보안 레벨 확인
        PwSecLevel pwSecLevel = PwChecker.checkPw(userPwUpdateFormDto.getUpdatePwChk());
        // 비밀번호 값이 유효하지 않은 경우
        if (pwSecLevel.equals(PwSecLevel.NOT)) {
            return getErrorResultResponseEntity(bindingResult, "updatePw", userPwUpdateFormDto, request);
        }
        if (bindingResult.hasErrors()) {
            setApiCaptcha(userPwUpdateFormDto);
        }
        // 보안레벨 저장 나중에 -> 보안 위험 표시할 떄 유용
        userPwUpdateFormDto.setPwSecLevel(pwSecLevel);
        HttpSession session = request.getSession(false);
        UserInfoDto userInfoDto = (UserInfoDto) session.getAttribute(RedisKeyDto.USER_KEY);
        try {

            CustomUserDetails userDetails = userHelpService.passwordUpdate(userPwUpdateFormDto, userInfoDto);
            //변경로그
            userActivityLogService.save(userDetails.getUId(), userDetails.getUsername(), "sky.pw",
                "sky.log.pw.update", request, ChangeSuccess.SUCCESS);
            //인증 이미지 삭제
            apiExamCaptchaNkeyService.deleteImage(userPwUpdateFormDto.getImageName());
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            userActivityLogService.save(null, userInfoDto.getUserId(), "sky.pw", "sky.log.pw.update",
                request, ChangeSuccess.FAIL);
            return getErrorResultResponseEntity(bindingResult, e.getMessage(), userPwUpdateFormDto, request);
        }
    }


    @PostMapping("/login/status")
    public ResponseEntity updateLoginStatus(HttpServletRequest request) {
        HttpSession session = request.getSession();
        UserInfoDto userInfoDto = (UserInfoDto) session.getAttribute(RedisKeyDto.USER_KEY);

        String check = userIdCheck(request);

        // 아이디가 있는 지 체크 유무
        if (check != null) {
            return Result.getErrorResult(
                new ErrorGlobalResultDto(check, ms, request.getLocale()));
        }
        try {
            userLoginStatusService.removeAllLoginStatus(userInfoDto.getUserId(),session.getId());
        }catch (Exception e) {
            return Result.getErrorResult(
                new ErrorGlobalResultDto("error", ms, request.getLocale()));
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private String userIdCheck(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            UserInfoDto userInfoDto = (UserInfoDto) session.getAttribute(RedisKeyDto.USER_KEY);
            userQueryService.findOne(userInfoDto.getUserId());
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
        return null;
    }


    private ResponseEntity<ErrorResult> getErrorResultResponseEntity(BindingResult bindingResult, String errorCode,
        UserPwUpdateFormDto userPwUpdateFormDto, HttpServletRequest request) throws IOException {
        bindingResult.reject(errorCode);
        apiExamCaptchaNkeyService.deleteImage(userPwUpdateFormDto.getImageName());
        return Result.getErrorResult(
            new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
    }

    private void setApiCaptcha(UserPwUpdateFormDto userPwUpdateFormDto) {
        Map<String, Object> apiExamCaptchaNkey = apiExamCaptchaNkeyService.getApiExamCaptchaNkey();
        String key = (String) apiExamCaptchaNkey.get("key");
        String apiExamCaptchaImage = apiExamCaptchaNkeyService.getApiExamCaptchaImage(key);
        userPwUpdateFormDto.setCaptchaKey(key);
        userPwUpdateFormDto.setImageName(apiExamCaptchaImage);
    }
}

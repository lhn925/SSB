package sky.board.domain.user.api.myInfo;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sky.board.domain.user.dto.UserInfoDto;
import sky.board.domain.user.dto.login.CustomUserDetails;
import sky.board.domain.user.dto.myInfo.UserLoginBlockUpdateDto;
import sky.board.domain.user.dto.myInfo.UserLoginListDto;
import sky.board.domain.user.dto.myInfo.UserLoginStatusUpdateDto;
import sky.board.domain.user.dto.myInfo.UserNameUpdateDto;
import sky.board.domain.user.dto.myInfo.UserPictureUpdateDto;
import sky.board.domain.user.dto.myInfo.UserPwUpdateFormDto;
import sky.board.domain.user.entity.UserActivityLog;
import sky.board.domain.user.entity.login.UserLoginStatus;
import sky.board.domain.user.model.ChangeSuccess;
import sky.board.domain.user.model.PwSecLevel;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.service.help.UserHelpService;
import sky.board.domain.user.service.log.UserActivityLogService;
import sky.board.domain.user.service.log.UserLoginLogService;
import sky.board.domain.user.service.login.UserLoginStatusService;
import sky.board.domain.user.service.myInfo.UserMyInfoService;
import sky.board.domain.user.utili.PwChecker;
import sky.board.global.error.dto.ErrorResultDto;
import sky.board.global.error.dto.Result;
import sky.board.global.file.dto.UploadFile;
import sky.board.global.file.utili.FileStore;
import sky.board.global.openapi.service.ApiExamCaptchaNkeyService;
import sky.board.global.redis.dto.RedisKeyDto;

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
    private final UserLoginLogService userLoginLogService;

    @PostMapping("/userName")
    public ResponseEntity updateUserName(@Validated @RequestBody UserNameUpdateDto userNameUpdateDto,
        BindingResult bindingResult,
        HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        // 중복체크
        userMyInfoService.updateUserName(request, userNameUpdateDto);
        return new ResponseEntity(new Result<>(userNameUpdateDto), HttpStatus.OK);
    }

    @PostMapping("/picture")
    public ResponseEntity updateUserProfilePicture(@Validated @ModelAttribute UserPictureUpdateDto file,
        BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        UploadFile uploadFile = null;
        uploadFile = userMyInfoService.updatePicture(request, file.getFile());
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
    public ResponseEntity deleteUserProfilePicture(HttpServletRequest request) throws FileNotFoundException {
        userMyInfoService.deletePicture(request);
        return ResponseEntity.ok(HttpStatus.OK);
    }


    @PostMapping("/pw")
    public ResponseEntity updateUserPassWord(@Validated @RequestBody UserPwUpdateFormDto userPwUpdateFormDto,
        BindingResult bindingResult, HttpServletRequest request) throws IOException {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }

        //디코딩
        byte[] dePw = Base64.getDecoder().decode(userPwUpdateFormDto.getPassword().getBytes());
        byte[] deNewPw = Base64.getDecoder().decode(userPwUpdateFormDto.getNewPw().getBytes());
        byte[] deNewPwChk = Base64.getDecoder().decode(userPwUpdateFormDto.getNewPwChk().getBytes());

        userPwUpdateFormDto.setPassword(new String(dePw, StandardCharsets.UTF_8));
        userPwUpdateFormDto.setNewPw(new String(deNewPw, StandardCharsets.UTF_8));
        userPwUpdateFormDto.setNewPwChk(new String(deNewPwChk, StandardCharsets.UTF_8));

        boolean isCaptcha;
        Map result = apiExamCaptchaNkeyService.getApiExamCaptchaNkeyResult(
            userPwUpdateFormDto.getCaptchaKey(), userPwUpdateFormDto.getCaptcha());

        isCaptcha = (boolean) result.get("result");
        // 비밀번호 보안 레벨 확인
        PwSecLevel pwSecLevel = PwChecker.checkPw(userPwUpdateFormDto.getNewPw());
        try {
            // 입력값 체크
            valueCheck(userPwUpdateFormDto, isCaptcha, pwSecLevel);
        } catch (IllegalArgumentException e) {
            setApiCaptcha(userPwUpdateFormDto);
            throw new IllegalArgumentException(e.getMessage());
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
            deleteImage(userPwUpdateFormDto);
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            userActivityLogService.save(null, userInfoDto.getUserId(), "sky.pw", "sky.log.pw.update",
                request, ChangeSuccess.FAIL);
            deleteImage(userPwUpdateFormDto);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @PostMapping("/login/status")
    public ResponseEntity updateLoginStatus(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        UserInfoDto userInfoDto = (UserInfoDto) session.getAttribute(RedisKeyDto.USER_KEY);
        userLoginStatusService.removeAllLoginStatus(userInfoDto.getUserId(), session.getId());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/login/status")
    public ResponseEntity logoutStatus(@Validated @RequestBody UserLoginStatusUpdateDto userLoginStatusUpdateDto,
        BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        // 로그인 되어 있는 디바이스 기기 로그아웃

        userLoginStatusService.logoutDevice(request, userLoginStatusUpdateDto.getSession(), Status.ON, Status.ON);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/block")
    public ResponseEntity loginBlockedUpdate(@Validated @RequestBody UserLoginBlockUpdateDto userLoginBlockDto,
        BindingResult bindingResult,
        HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        userMyInfoService.updateLoginBlocked(userLoginBlockDto, request);
        return ResponseEntity.ok(HttpStatus.OK);
    }


    @GetMapping("/loginDevice")
    public ResponseEntity getLoginList(@RequestParam(name = "offset", defaultValue = "0") Integer offset,
        @RequestParam(name = "size", defaultValue = "10", required = false) Integer size, HttpServletRequest request) {
        PageRequest pageRequest = PageRequest.of(offset, size, Sort.by(Direction.DESC, "id"));

        Page<UserLoginListDto> pagingStatusList = userLoginStatusService.getUserLoginStatusList(request, Status.ON,
            pageRequest);
        return ResponseEntity.ok(new Result<>(pagingStatusList));
    }


    @GetMapping("/userLoginLog")
    public ResponseEntity getLoginLogList(
        @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
        @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
        @RequestParam(name = "offset", defaultValue = "0") Integer offset,
        @RequestParam(name = "size", defaultValue = "10") Integer size, HttpServletRequest request) {
        PageRequest pageRequest = PageRequest.of(offset, size, Sort.by(Direction.DESC, "id"));
        if (endDate == null || startDate == null) { // 조회할려는 날짜가 없을 경우
            startDate = LocalDate.now().minusDays(7);
            endDate = LocalDate.now();
        }
        Page pagingLoginLoginList = userLoginLogService.getUserLoginLogList(request, startDate, endDate, pageRequest);
        return ResponseEntity.ok(new Result<>(pagingLoginLoginList));
    }

    @GetMapping("/userActivityLog")
    public ResponseEntity getActivityLogList(
        @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
        @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
        @RequestParam(name = "offset", defaultValue = "0") Integer offset,
        @RequestParam(name = "size", defaultValue = "10") Integer size, HttpServletRequest request) {
        PageRequest pageRequest = PageRequest.of(offset, size, Sort.by(Direction.DESC, "id"));
        if (endDate == null || startDate == null) { // 조회할려는 날짜가 없을 경우
            startDate = LocalDate.now().minusDays(7);
            endDate = LocalDate.now();
        }

        Page pagingLoginLoginList = userActivityLogService.getUserActivityLogList(request, ChangeSuccess.SUCCESS,
            Status.ON, startDate, endDate, pageRequest);
        return ResponseEntity.ok(new Result<>(pagingLoginLoginList));
    }

    private static void valueCheck(UserPwUpdateFormDto userPwUpdateFormDto, boolean isCaptcha, PwSecLevel pwSecLevel) {
        if (!isCaptcha) {
            throw new IllegalArgumentException("error.captcha");
        }
        // 확인 비밀번호가 불일치 할 경우
        if (!userPwUpdateFormDto.getNewPw().equals(userPwUpdateFormDto.getNewPwChk())) {
            throw new IllegalArgumentException("pw.mismatch");
        }
        // 비밀번호 값이 유효하지 않은 경우
        if (pwSecLevel.equals(PwSecLevel.NOT)) {
            throw new IllegalArgumentException("updatePw");
        }
    }

    private void deleteImage(
        UserPwUpdateFormDto userPwUpdateFormDto) throws IOException {
        apiExamCaptchaNkeyService.deleteImage(userPwUpdateFormDto.getImageName());
    }


    private void setApiCaptcha(UserPwUpdateFormDto userPwUpdateFormDto) {
        Map<String, Object> apiExamCaptchaNkey = apiExamCaptchaNkeyService.getApiExamCaptchaNkey();
        String key = (String) apiExamCaptchaNkey.get("key");
        String apiExamCaptchaImage = apiExamCaptchaNkeyService.getApiExamCaptchaImage(key);
        userPwUpdateFormDto.setCaptchaKey(key);
        userPwUpdateFormDto.setImageName(apiExamCaptchaImage);
    }
}

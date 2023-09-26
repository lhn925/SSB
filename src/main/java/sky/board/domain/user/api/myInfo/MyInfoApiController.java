package sky.board.domain.user.api.myInfo;


import static java.time.format.DateTimeFormatter.ISO_DATE;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
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
import sky.board.global.file.dto.UploadFileDto;
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

    /**
     * id:myInfo_Api_1
     * 유저 닉네임 업데이트
     *
     * @param userNameUpdateDto
     * @param bindingResult
     * @param request
     * @return
     */
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

    /**
     * id:myInfo_Api_2
     * <p>
     * 유저 프로필 사진 업데이트
     *
     * @param file
     * @param bindingResult
     * @param request
     * @return 파일을 업로드할때 RequestBody를 사용하면 Exception 발생
     */
    @PostMapping("/picture")
    public ResponseEntity updateUserProfilePicture(@Validated @ModelAttribute UserPictureUpdateDto file,
        BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        UploadFileDto uploadFileDto = null;
        uploadFileDto = userMyInfoService.updatePicture(request, file.getFile());
        return new ResponseEntity(new Result<>(uploadFileDto), HttpStatus.OK);
    }


  /*  /**
     * id:myInfo_Api_3
     * <p>
     * 서버에서 프로필 이미지 가져오기
     *
     * @param fileName
     * @param request
     * @return
     * @throws IOException
     *//*
    @GetMapping("/picture/{fileName}")
    public ResponseEntity getUserProfilePicture(@PathVariable String fileName, HttpServletRequest request)
        throws IOException {

        HttpSession session = request.getSession(false);
        UserInfoDto userInfoDto = (UserInfoDto) session.getAttribute(RedisKeyDto.USER_KEY);

        // file MediaType 확인 후 header 에 저장
        MediaType mediaType = null;
        UrlResource pictureImage = null;
        mediaType = MediaType.parseMediaType(Files.probeContentType(Paths.get(fileName)));
        log.info("mediaType = {}", mediaType);
        pictureImage = userMyInfoService.getPictureImage(
            FileStore.USER_PICTURE_DIR + userInfoDto.getToken() + "/" + fileName);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, mediaType.toString())
            .body(pictureImage);

    }*/

/*    @GetMapping("/picture/default")
    public ResponseEntity<Resource> getUserProfilePictureDefault() throws IOException {
        MediaType mediaType = MediaType.parseMediaType(
            Files.probeContentType(Paths.get(FileStore.USER_DEFAULT_IMAGE_URL)));
        UrlResource pictureImage = userMyInfoService.getPictureImage(FileStore.USER_DEFAULT_IMAGE_URL);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, mediaType.toString())
            .body(pictureImage);
    }*/

    /**
     * id:myInfo_Api_3
     * 프로필 이미지 삭제
     * @param request
     * @return
     * @throws FileNotFoundException
     */
    @DeleteMapping("/picture")
    public ResponseEntity deleteUserProfilePicture(HttpServletRequest request) throws FileNotFoundException {
        userMyInfoService.deletePicture(request);
        return ResponseEntity.ok(HttpStatus.OK);
    }


    /**
     *
     * 비밀번호 수정
     * id:myInfo_Api_4
     * @param userPwUpdateFormDto
     * @param bindingResult
     * @param request
     * @return
     * @throws IOException
     */
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

    /**
     * id:myInfo_api_5
     * 비밀번호 변경 후
     * 해당아이디에 접속되어 있는 기기 전부 다 로그아웃
     * @param request
     * @return
     */
    @PostMapping("/login/status")
    public ResponseEntity updateLoginStatus(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        UserInfoDto userInfoDto = (UserInfoDto) session.getAttribute(RedisKeyDto.USER_KEY);
        userLoginStatusService.removeAllLoginStatus(userInfoDto.getUserId(), session.getId());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    /**
     * id:myInfo_api_6
     * 해당 기기 원격 로그아웃
     * @param userLoginStatusUpdateDto
     * @param bindingResult
     * @param request
     * @return
     */
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

    /**
     * id:myInfo_api_7
     * 해외 로그인 차단 설정 변경
     * @param userLoginBlockDto
     * @param bindingResult
     * @param request
     * @return
     */
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


    /**
     * id:myInfo_api_8
     * 로그인 되어 있는 기기 목록 검색 후 전달
     * @param offset
     * @param size
     * @param request
     * @return
     */
    @GetMapping("/loginDevice")
    public ResponseEntity getLoginList(@RequestParam(name = "offset", defaultValue = "0") Integer offset,
        @RequestParam(name = "size", defaultValue = "2", required = false) Integer size, HttpServletRequest request) {
        PageRequest pageRequest = PageRequest.of(offset, size, Sort.by(Direction.DESC, "id"));

        Page<UserLoginListDto> pagingStatusList = userLoginStatusService.getUserLoginStatusList(request, Status.ON,
            pageRequest);
        return ResponseEntity.ok(new Result<>(pagingStatusList));
    }


    /**
     * id:myInfo_api_9
     *
     * type 과 날짜 에 따라 유저정보 변경 및 유저 로그인 로그 목록 검색 후 목록 전달
     *
     *
     * @param type
     * @param startDate
     * @param endDate
     * @param offset
     * @param size
     * @param request
     * @return
     */
    @GetMapping("/userLog")
    public ResponseEntity getLoginLogList(
        @RequestParam(value = "type", required = false) String type,
        @RequestParam(value = "startDate", required = false) String startDate,
        @RequestParam(value = "endDate", required = false) String endDate,
        @RequestParam(name = "offset", defaultValue = "0") Integer offset,
        @RequestParam(name = "size", defaultValue = "2") Integer size, HttpServletRequest request) {
        PageRequest pageRequest = PageRequest.of(offset, size, Sort.by(Direction.DESC, "id"));
        LocalDate start = null;
        LocalDate end = null;

        if (type == null || !StringUtils.hasText("type")) {
            type = "userLoginLog";
        }

        // 조회 최대 날짜는 현재 날짜까지만 가능
        LocalDate maxDate = LocalDate.now();

        // userLoginLog는 3개월 전까지만 조회 가능
        // userActivityLog는 6개월 전까지만 조회가능
        LocalDate minDate = null;
        int minNumber = 6;
        if (type.equals("userLoginLog")) {
            minNumber = 3;
        }

        minDate = LocalDate.now().minusMonths(minNumber);

        // 기본 값 설정
        start = LocalDate.parse(LocalDate.now().minusDays(7).format(ISO_DATE));
        end = LocalDate.parse(LocalDate.now().format(ISO_DATE));

        try {
            if (startDate != null || !startDate.equals("")) { // 조회할려는 날짜가 없을 경우
                start = LocalDate.parse(startDate, ISO_DATE);
            }
            if (endDate != null || !endDate.equals("")) { // 조회할려는 날짜가 없을 경우
                end = LocalDate.parse(endDate, ISO_DATE);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("date.error.range");
        }
        // maxDate가 endDate 보다 이전 날짜냐? true
        // 조회할려는 날짜가 최대 날짜 보다 크다면
        // 조회 할수 있는 최소 날짜 보다
        // 작다면 minDate가 startDate 보다 앞에 날짜냐
        if (maxDate.isBefore(end) || minDate.isAfter(start)) {
            start = LocalDate.parse(LocalDate.now().minusDays(7).format(ISO_DATE));
            end = LocalDate.parse(LocalDate.now().format(ISO_DATE));
        }

        Page pagingLoginList;
        if (type.equals("userLoginLog")) {
            pagingLoginList = userLoginLogService.getUserLoginLogList(request, start, end, pageRequest);
        } else {
            pagingLoginList = userActivityLogService.getUserActivityLogList(request, ChangeSuccess.SUCCESS,
                Status.ON, start, end, pageRequest);
        }
        return ResponseEntity.ok(new Result<>(pagingLoginList));

    }

/*    @GetMapping("/userActivityLog")
    public ResponseEntity getActivityLogList(
        @RequestParam(value = "startDate", required = false)  String startDate,
        @RequestParam(value = "endDate", required = false)  String endDate,
        @RequestParam(name = "offset", defaultValue = "0") Integer offset,
        @RequestParam(name = "size", defaultValue = "2") Integer size, HttpServletRequest request) {
        PageRequest pageRequest = PageRequest.of(offset, size, Sort.by(Direction.DESC, "id"));
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = null;
        LocalDate end = null;

        if (startDate == null || StringUtils.hasText(startDate)) { // 조회할려는 날짜가 없을 경우
            start = LocalDate.parse(LocalDate.now().minusDays(7).format(dateTimeFormatter));
        } else {
            start = LocalDate.parse(startDate, dateTimeFormatter);
        }
        if (endDate == null || StringUtils.hasText(endDate)) { // 조회할려는 날짜가 없을 경우
            end = LocalDate.parse(LocalDate.now().format(dateTimeFormatter));
        } else {
            end = LocalDate.parse(endDate, dateTimeFormatter);
        }

        Page pagingLoginLoginList = userActivityLogService.getUserActivityLogList(request, ChangeSuccess.SUCCESS,
            Status.ON, start, end, pageRequest);
        return ResponseEntity.ok(new Result<>(pagingLoginLoginList));
    }*/

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

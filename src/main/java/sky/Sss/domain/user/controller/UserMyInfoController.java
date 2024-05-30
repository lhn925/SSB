package sky.Sss.domain.user.controller;


import static java.time.format.DateTimeFormatter.ISO_DATE;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
import sky.Sss.domain.user.annotation.UserAuthorize;
import sky.Sss.domain.user.dto.UserInfoDto;
import sky.Sss.domain.user.dto.myInfo.UserLoginBlockUpdateDto;
import sky.Sss.domain.user.dto.myInfo.UserLoginListDto;
import sky.Sss.domain.user.dto.myInfo.UserLoginStatusUpdateDto;
import sky.Sss.domain.user.dto.myInfo.UserMyInfoDto;
import sky.Sss.domain.user.dto.myInfo.UserNameUpdateDto;
import sky.Sss.domain.user.dto.myInfo.UserPictureUpdateDto;
import sky.Sss.domain.user.dto.myInfo.UserPwUpdateFormDto;
import sky.Sss.domain.user.exception.DuplicateCheckException;
import sky.Sss.domain.user.model.ChangeSuccess;
import sky.Sss.domain.user.model.PwSecLevel;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.model.UserLogType;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.domain.user.service.help.UserHelpService;
import sky.Sss.domain.user.service.log.UserActivityLogService;
import sky.Sss.domain.user.service.log.UserLoginLogService;
import sky.Sss.domain.user.service.login.UserLoginStatusService;
import sky.Sss.domain.user.service.myInfo.UserMyInfoService;
import sky.Sss.domain.user.service.profile.UserProfileService;
import sky.Sss.domain.user.utili.PwChecker;
import sky.Sss.global.error.dto.ErrorResultDto;
import sky.Sss.global.error.dto.FieldErrorCustom;
import sky.Sss.global.error.dto.Result;
import sky.Sss.global.file.dto.UploadFileDto;
import sky.Sss.global.openapi.service.ApiExamCaptchaNkeyService;
import sky.Sss.global.redis.dto.RedisKeyDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/info")
@UserAuthorize
public class UserMyInfoController {
    private final UserQueryService userQueryService;
    private final ApiExamCaptchaNkeyService apiExamCaptchaNkeyService;
    private final MessageSource ms;
    private final UserMyInfoService userMyInfoService;
    private final UserActivityLogService userActivityLogService;
    private final UserHelpService userHelpService;
    private final UserLoginStatusService userLoginStatusService;
    private final UserLoginLogService userLoginLogService;
    private final UserProfileService userProfileService;


    /**
     * id:myInfo_1
     *
     * @return
     */
    @GetMapping
    public ResponseEntity<UserMyInfoDto> userProfileForm() {
        // 유저 정보 조회
        return new ResponseEntity<>(userProfileService.getUserMyInfoDto(), HttpStatus.OK);
    }

    /**
     * id:myInfo_2
     *
     * @return
     */
    @GetMapping("/pw")
    public ResponseEntity<?>  pwUpdateForm() {
        // 유저 정보 조회
        userQueryService.findOne();

        Map<String, Object> apiExamCaptchaNkey = apiExamCaptchaNkeyService.getApiExamCaptchaNkey();
        String key = (String) apiExamCaptchaNkey.get("key");
        String apiExamCaptchaImage = apiExamCaptchaNkeyService.getApiExamCaptchaImage(key);
        // 유저 정보 반환
        UserPwUpdateFormDto userPwUpdateFormDto = UserPwUpdateFormDto.builder()
            .captchaKey(key)
            .imageName(apiExamCaptchaImage).build();
        return new ResponseEntity(new Result<>(userPwUpdateFormDto), HttpStatus.OK);
    }

    /**
     * id:myInfo_Api_1
     * 유저 닉네임 업데이트
     *
     * @param userNameUpdateDto
     * @param bindingResult
     * @param request
     * @return
     */
    @PostMapping("/username")
    public ResponseEntity<?>  updateUserName(@Validated @RequestBody UserNameUpdateDto userNameUpdateDto,
        BindingResult bindingResult,
        HttpServletRequest request) throws DuplicateCheckException {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        // 중복체크
        userMyInfoService.updateUserName(userNameUpdateDto, bindingResult);
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
    public ResponseEntity<?>  updateUserProfilePicture(@Validated @ModelAttribute UserPictureUpdateDto file,
        BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        UploadFileDto uploadFileDto = null;
        uploadFileDto = userMyInfoService.updatePicture(file.getFile());
        return new ResponseEntity<>(new Result<>(uploadFileDto), HttpStatus.OK);
    }

    /**
     * id:myInfo_Api_3
     * 프로필 이미지 삭제
     *
     * @return
     * @throws FileNotFoundException
     */
    @DeleteMapping("/picture")
    public ResponseEntity<?>  deleteUserProfilePicture() throws FileNotFoundException {
        userMyInfoService.deletePicture();
        return ResponseEntity.ok(HttpStatus.OK);
    }
    /**
     * 비밀번호 수정
     * id:myInfo_Api_4
     *
     * @param userPwUpdateFormDto
     * @param bindingResult
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/pw")
    public ResponseEntity<?>  updateUserPassWord(@Validated @RequestBody UserPwUpdateFormDto userPwUpdateFormDto,
        BindingResult bindingResult ,HttpServletRequest request) throws IOException {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        boolean isCaptcha;
        Map<String,Object> result = apiExamCaptchaNkeyService.getApiExamCaptchaNkeyResult(
            userPwUpdateFormDto.getCaptchaKey(), userPwUpdateFormDto.getCaptcha());

        isCaptcha = (boolean) result.get("result");
        // 비밀번호 보안 레벨 확인
        PwSecLevel pwSecLevel = PwChecker.checkPw(userPwUpdateFormDto.getNewPw());
        // 자동입력 방지 번호가 맞지 않은 경우
        if (!isCaptcha) {
            addError(bindingResult, "userPwUpdateFormDto", "captcha", null, "error.captcha");
        }
        // 확인 비밀번호가 불일치 할 경우
        if (!userPwUpdateFormDto.getNewPw().equals(userPwUpdateFormDto.getNewPwChk())) {
            addError(bindingResult, "userPwUpdateFormDto", "newPwChk", userPwUpdateFormDto.getNewPw(), "pw.mismatch");
        }
        // 비밀번호 보안 레벨 확인
        // 비밀번호 값이 유효하지 않은 경우
        if (pwSecLevel.equals(PwSecLevel.NOT)) {
            addError(bindingResult, "userPwUpdateFormDto", "newPw", userPwUpdateFormDto.getNewPw(),
                "userJoinForm.password");
        }
        if (bindingResult.hasErrors()) {
            setApiCaptcha(userPwUpdateFormDto);
            addCaptchaError(userPwUpdateFormDto, bindingResult);
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        // 보안레벨 저장 나중에 -> 보안 위험 표시할 떄 유용
        userPwUpdateFormDto.setPwSecLevel(pwSecLevel);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        try {
            userHelpService.myPagePwUpdate(userPwUpdateFormDto);
            //변경로그
            userActivityLogService.add(userDetails.getUsername(), "sky.pw",
                "sky.log.pw.update", request.getHeader("User-Agent"), ChangeSuccess.SUCCESS);
            //인증 이미지 삭제
            deleteImage(userPwUpdateFormDto);

            // true 일 경우 현재 기기를 제외한 모든 세션 로그아웃
            if (userPwUpdateFormDto.isLogoutChk()) {
                userLoginStatusService.removeStatusNotSession
                    (userDetails.getUsername(),request.getSession().getId());
            }
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (BadCredentialsException | IllegalArgumentException e) {
            setApiCaptcha(userPwUpdateFormDto);
            addCaptchaError(userPwUpdateFormDto, bindingResult);
            userActivityLogService.add(userDetails.getUsername(), "sky.pw", "sky.log.pw.update",
                request.getHeader("User-Agent"), ChangeSuccess.FAIL);
            String field = "newPw";
            String rejectValue = userPwUpdateFormDto.getNewPw();
            // 오류가 원래 비밀번호가 맞지 않는 경우
            if (e instanceof BadCredentialsException) {
                field = "password";
                rejectValue = userPwUpdateFormDto.getPassword();
            }
            addError(bindingResult, "userPwUpdateFormDto", field, rejectValue, e.getMessage());
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
    }

    /**
     * id:myInfo_api_5
     * 비밀번호 변경 후
     * 해당아이디에 접속되어 있는 기기 전부 다 로그아웃
     *
     * @param request
     * @return
     */
    @PostMapping("/login/status")
    public ResponseEntity<?>  updateLoginStatus(HttpServletRequest request) {
        HttpSession session = request.getSession();

        UserInfoDto userInfoDto = (UserInfoDto) session.getAttribute(RedisKeyDto.REDIS_USER_KEY);
        userLoginStatusService.removeAllStatus(userInfoDto.getUserId(), session.getId());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    /**
     * id:myInfo_api_6
     * 해당 기기 원격 로그아웃
     *
     * @param userLoginStatusUpdateDto
     * @param bindingResult
     * @param request
     * @return
     */
    @PatchMapping("/logout/status")
    public ResponseEntity<?>  logoutStatus(@Validated @RequestBody UserLoginStatusUpdateDto userLoginStatusUpdateDto,
        BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        String sessionId = request.getSession().getId();
        // 로그인 되어 있는 디바이스 기기 로그아웃
        userLoginStatusService.logoutDevice(userLoginStatusUpdateDto.getPassword(),userLoginStatusUpdateDto.getSession(), Status.ON, Status.ON, sessionId);
        return ResponseEntity.ok(HttpStatus.OK);
    }




    /**
     * id:myInfo_api_7
     * 해외 로그인 차단 설정 변경
     *
     * @param userLoginBlockDto
     * @param bindingResult
     * @param request
     * @return
     */
    @PostMapping("/blocked")
    public ResponseEntity<?>  loginBlockedUpdate(@Validated @RequestBody UserLoginBlockUpdateDto userLoginBlockDto,
        BindingResult bindingResult,
        HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        userMyInfoService.updateLoginBlocked(userLoginBlockDto);
        return ResponseEntity.ok(HttpStatus.OK);
    }


    /**
     * id:myInfo_api_8
     * 로그인 되어 있는 기기 목록 검색 후 전달
     *
     * @param offset
     * @param size
     * @param request
     * @return
     */
    @GetMapping("/login-device")
    public ResponseEntity<?>  getLoginList(@RequestParam(name = "offset", defaultValue = "0") Integer offset,
        @RequestParam(name = "size", defaultValue = "5", required = false) Integer size, HttpServletRequest request) {
        Page<UserLoginListDto> pagingStatusList = userLoginStatusService.getUserLoginStatusList(
            request.getSession().getId(), Status.ON,offset,size);
        return ResponseEntity.ok(pagingStatusList);
    }


    /**
     * id:myInfo_api_9
     * <p>
     * type 과 날짜 에 따라 유저정보 변경 및 유저 로그인 로그 목록 검색 후 목록 전달
     *
     * @param type
     * @param startDate
     * @param endDate
     * @param offset
     * @param size
     * @param request
     * @return
     */
    @GetMapping("/user-log")
    public ResponseEntity<?>  getLoginLogList(
        @RequestParam(name = "type") String type,
        @RequestParam(name = "startDate", required = false) String startDate,
        @RequestParam(name = "endDate", required = false) String endDate,
        @RequestParam(name = "offset", defaultValue = "0") Integer offset,
        @RequestParam(name = "size", defaultValue = "5") Integer size, HttpServletRequest request) {
        PageRequest pageRequest = PageRequest.of(offset, size, Sort.by(Direction.DESC, "id"));
        LocalDate start = null;
        LocalDate end = null;

        UserLogType userLogType = UserLogType.findByType(type);

        // 조회 최대 날짜는 현재 날짜까지만 가능
        LocalDate maxDate = LocalDate.now();

        // userLoginLog는 3개월 전까지만 조회 가능
        // userActivityLog는 6개월 전까지만 조회가능
        LocalDate minDate = null;
        int minNumber = 6;
        if (userLogType.equals(UserLogType.HISTORY_LOGIN_LOG)) {
            minNumber = 3;
        }

        minDate = LocalDate.now().minusMonths(minNumber);

        // 기본 값 설정
        start = LocalDate.parse(LocalDate.now().minusDays(7).format(ISO_DATE));
        end = LocalDate.parse(LocalDate.now().format(ISO_DATE));

        try {
            if (startDate != null && !startDate.equals("")) { // 조회할려는 날짜가 없을 경우
                start = LocalDate.parse(startDate, ISO_DATE);
            }
            if (endDate != null && !endDate.equals("")) { // 조회할려는 날짜가 없을 경우
                end = LocalDate.parse(endDate, ISO_DATE);
            }
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }

        // maxDate가 endDate 보다 이전 날짜냐? true
        // 조회할려는 날짜가 최대 날짜 보다 크다면
        // 조회 할수 있는 최소 날짜 보다
        // 작다면 minDate가 startDate 보다 앞에 날짜냐
        if (maxDate.isBefore(end) || minDate.isAfter(start)) {
            start = LocalDate.parse(LocalDate.now().minusDays(7).format(ISO_DATE));
            end = LocalDate.parse(LocalDate.now().format(ISO_DATE));
        }

        Page pagingLoginList = null;
        if (userLogType.equals(UserLogType.HISTORY_LOGIN_LOG)) {
            pagingLoginList = userLoginLogService.getUserLoginLogList(start, end, pageRequest);
        } else {
            pagingLoginList = userActivityLogService.getUserActivityLogList(ChangeSuccess.SUCCESS,
                Status.ON, start, end, pageRequest, request.getLocale());
        }
        return ResponseEntity.ok(pagingLoginList);

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

    private void addError(BindingResult bindingResult, String userPwUpdateFormDto, String field,
        String rejectValue, String code) {
        bindingResult.addError(
            new FieldErrorCustom(userPwUpdateFormDto,
                field, rejectValue,
                code,
                null));
    }

    private void addCaptchaError(UserPwUpdateFormDto userPwUpdateFormDto, BindingResult bindingResult) {
        addError(bindingResult, "userPwUpdateFormDto", "captchaKey", userPwUpdateFormDto.getCaptchaKey(),
            "error.captcha");
        addError(bindingResult, "imageName", "imageName", userPwUpdateFormDto.getImageName(),
            "error.captcha");
    }

}

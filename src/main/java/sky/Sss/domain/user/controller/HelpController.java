package sky.Sss.domain.user.controller;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.email.dto.EmailAuthCodeDto;
import sky.Sss.domain.user.dto.help.UserHelpDto;
import sky.Sss.domain.user.dto.help.UserIdQueryDto;
import sky.Sss.domain.user.dto.help.UserPwResetFormDto;
import sky.Sss.domain.user.dto.login.CustomUserDetails;
import sky.Sss.domain.user.exception.UserInfoNotFoundException;
import sky.Sss.domain.user.model.ChangeSuccess;
import sky.Sss.domain.user.model.Enabled;
import sky.Sss.domain.user.model.HelpType;
import sky.Sss.domain.user.model.PwSecLevel;
import sky.Sss.domain.user.service.help.UserHelpService;
import sky.Sss.domain.user.service.log.UserActivityLogService;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.domain.user.service.login.UserLoginStatusService;
import sky.Sss.domain.user.utili.CustomCookie;
import sky.Sss.domain.user.utili.PwChecker;
import sky.Sss.global.error.dto.ErrorGlobalResultDto;
import sky.Sss.global.error.dto.ErrorResultDto;
import sky.Sss.global.error.dto.FieldErrorCustom;
import sky.Sss.global.error.dto.Result;
import sky.Sss.global.openapi.service.ApiExamCaptchaNkeyService;
import sky.Sss.global.utili.Alert;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/help")
public class HelpController {

    private final UserQueryService userQueryService;

    private final UserLoginStatusService userLoginStatusService;
    private final UserActivityLogService userActivityLogService;

    private final UserHelpService userHelpService;
    private final MessageSource ms;

    private final ApiExamCaptchaNkeyService apiExamCaptchaNkeyService;

    /**
     * id:help_3
     *
     * @param userHelpDto
     * @param request
     * @return
     * @throws IOException
     */
    @GetMapping("/show")
    public ResponseEntity showId(@RequestBody @Validated UserHelpDto userHelpDto, BindingResult bindingResult,
        HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }

        HttpSession session = request.getSession();
        EmailAuthCodeDto emailAuthCodeDto = (EmailAuthCodeDto) session.getAttribute("emailAuthCodeDto");

        CustomUserDetails findOne = userQueryService.findByEmailOne(emailAuthCodeDto.getEmail(), Enabled.ENABLED);

        /**
         * 이메일 인증에 성공하지 못했으면
         */
        if (emailAuthCodeDto == null || !emailAuthCodeDto.getIsSuccess()) {
            session.removeAttribute("emailAuthCodeDto");
            addError(bindingResult, "userHelpDto", "email", userHelpDto.getEmail(), "userJoinForm.email2");
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        userHelpDto.setCreatedDateTime(findOne.getCreatedDateTime());
        return new ResponseEntity(new Result<>(userHelpDto), HttpStatus.OK);
    }

    /**
     * id:help_5
     * <p>
     * 비밀번호 찾기 시 아이디가 있는지 체크
     *
     * @param userId
     * @param request
     * @return
     * @throws IOException
     */
    @GetMapping("/idQuery")
    public ResponseEntity idQuery(@Validated @RequestBody UserIdQueryDto userId, BindingResult bindingResult,
        HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        try {
            CustomUserDetails userDetails = userQueryService.findStatusUserId(userId.getUserId(),
                Enabled.ENABLED);
            // HelpToken 생성 및 세션 저장
            UserHelpDto userHelpDto = UserHelpDto.createUserHelpDto();
            userHelpDto.setUserId(userId.getUserId());

            StringBuffer email = new StringBuffer(userDetails.getEmail());

            // 2221325@naver.com -> 22*****@n****.com
            anonymousEmail(email);
            userHelpDto.setEnEmail(email.toString());
            userHelpDto.setUserId(userId.getUserId());

            return new ResponseEntity(new Result<>(userHelpDto), HttpStatus.OK);
        } catch (IllegalStateException | UsernameNotFoundException e) {
            throw new UsernameNotFoundException("userId.notfound");
        }
    }


    /**
     * id:help_6
     * 이메일 인증 후 비밀번호 재설정 페이지로 이동
     *
     * @param userHelpDto
     * @param request
     * @return
     * @throws IOException
     */
    @GetMapping("/reset")
    public ResponseEntity pwResetForm(@RequestBody UserHelpDto userHelpDto,
        HttpServletRequest request) throws IOException {
        // helpToken 쿠키가 만료됐거나 , 쿠키에 저장된 토큰과 요청한 토큰이 안 맞을 경우

        if (userIdCheck(userHelpDto.getUserId())) {
            throw new UsernameNotFoundException("userId.notfound");
        }
        HttpSession session = request.getSession();
        EmailAuthCodeDto emailAuthCodeDto = (EmailAuthCodeDto) session.getAttribute("emailAuthCodeDto");
        /**
         * 이메일 인증에 성공하지 못했으면
         */
        if (emailAuthCodeDto == null || !emailAuthCodeDto.getIsSuccess()) {
            session.removeAttribute("emailAuthCodeDto");
            return Result.getErrorResult(new ErrorResultDto("email", "userJoinForm.email2", ms, request.getLocale()));
        }

        Map<String, Object> apiExamCaptchaNkey = apiExamCaptchaNkeyService.getApiExamCaptchaNkey();
        String key = (String) apiExamCaptchaNkey.get("key");
        String apiExamCaptchaImage = apiExamCaptchaNkeyService.getApiExamCaptchaImage(key);
        UserPwResetFormDto userPwResetFormDto = UserPwResetFormDto.builder()
            .userId(userHelpDto.getUserId())
            .captchaKey(key)
            .imageName(apiExamCaptchaImage).build();
        return new ResponseEntity(new Result<>(userPwResetFormDto), HttpStatus.OK);
    }

    /**
     * id:help_7
     * 비밀번호 재설정 후 성공 시 logout 페이지로 이동
     *
     * @param userPwResetFormDto
     * @param bindingResult
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/reset")
    public ResponseEntity pwReset(@Validated @RequestBody UserPwResetFormDto userPwResetFormDto,
        BindingResult bindingResult,
        HttpServletRequest request)
        throws IOException {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }

        if (userIdCheck(userPwResetFormDto.getUserId())) {
            throw new UsernameNotFoundException("userId.notfound");
        }

        boolean isCaptcha;
        Map result = apiExamCaptchaNkeyService.getApiExamCaptchaNkeyResult(
            userPwResetFormDto.getCaptchaKey(), userPwResetFormDto.getCaptcha());

        isCaptcha = (boolean) result.get("result");

        // 자동입력 방지 번호가 맞지 않은 경우
        if (!isCaptcha) {
            addError(bindingResult, "userPwResetFormDto", "captcha", null, "error.captcha");
        }
        // 확인 비밀번호가 불일치 할 경우
        if (!userPwResetFormDto.getNewPw().equals(userPwResetFormDto.getNewPwChk())) {
            addError(bindingResult, "userPwResetFormDto", "newPwChk", userPwResetFormDto.getNewPw(), "pw.mismatch");
        }

        // 비밀번호 보안 레벨 확인
        PwSecLevel pwSecLevel = PwChecker.checkPw(userPwResetFormDto.getNewPw());
        // 비밀번호 값이 유효하지 않은 경우
        if (pwSecLevel.equals(PwSecLevel.NOT)) {
            addError(bindingResult, "userPwResetFormDto", "newPw", userPwResetFormDto.getNewPw(),
                "userJoinForm.password");
        }

        if (bindingResult.hasErrors()) {
            setApiCaptcha(userPwResetFormDto);
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }

        // 보안레벨 저장 나중에 -> 보안 위험 표시할 떄 유용
        userPwResetFormDto.setPwSecLevel(pwSecLevel);
        try {
            CustomUserDetails userDetails = (CustomUserDetails) userHelpService.passwordUpdate(userPwResetFormDto);
            //변경로그
            userActivityLogService.save(userDetails.getUsername(), "sky.pw",
                "sky.log.pw.chaContent", request.getHeader("User-Agent"), ChangeSuccess.SUCCESS);
            // 비밀번호가 전과 같을시에 IllegalArgumentException

            // 로그인된 기기 로그아웃
            userLoginStatusService.removeAllLoginStatus(userDetails.getUserId());

            //인증 이미지 삭제
            apiExamCaptchaNkeyService.deleteImage(userPwResetFormDto.getImageName());
            return new ResponseEntity(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            setApiCaptcha(userPwResetFormDto);
            userActivityLogService.save( userPwResetFormDto.getUserId(), "sky.pw", "sky.log.pw.chaContent",
                request.getHeader("User-Agent"), ChangeSuccess.FAIL);
            addError(bindingResult, "userPwResetFormDto", "newPw", userPwResetFormDto.getNewPw(), e.getMessage());
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
    }

    private void addError(BindingResult bindingResult, String userPwResetFormDto, String newPw,
        String userPwResetFormDto1, String code) {
        bindingResult.addError(
            new FieldErrorCustom(userPwResetFormDto,
                newPw, userPwResetFormDto1,
                code,
                null));
    }

    private boolean userIdCheck(String userId) throws IOException {
        try {
            userQueryService.findOne(userId);
        } catch (UserInfoNotFoundException e) { //유저를 찾을 수 없는 경우
            return true;
        }
        return false;
    }

    private void setApiCaptcha(UserPwResetFormDto userPwResetFormDto) throws IOException {
        log.info("setApiCaptcha= {}", userPwResetFormDto.getImageName());
        apiExamCaptchaNkeyService.deleteImage(userPwResetFormDto.getImageName());

        Map<String, Object> apiExamCaptchaNkey = apiExamCaptchaNkeyService.getApiExamCaptchaNkey();
        String key = (String) apiExamCaptchaNkey.get("key");
        String apiExamCaptchaImage = apiExamCaptchaNkeyService.getApiExamCaptchaImage(key);
        userPwResetFormDto.setCaptchaKey(key);
        userPwResetFormDto.setImageName(apiExamCaptchaImage);
    }


    // 2221325@naver.com -> 22*****@n****.com
    private void anonymousEmail(StringBuffer email) {
        int one = email.lastIndexOf("@");
        int two = email.lastIndexOf(".");
        for (int i = 2; i < email.length(); i++) {
            if (i == one || i == one + 1) {
                continue;
            }
            if (i == two) {
                break;
            }
            email.setCharAt(i, '*');
        }
    }

}

package sky.board.domain.user.controller;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sky.board.domain.email.dto.EmailAuthCodeDto;
import sky.board.domain.user.dto.help.UserHelpDto;
import sky.board.domain.user.dto.help.UserIdHelpQueryDto;
import sky.board.domain.user.dto.help.UserPwResetFormDto;
import sky.board.domain.user.dto.login.CustomUserDetails;
import sky.board.domain.user.model.ChangeSuccess;
import sky.board.domain.user.model.HelpType;
import sky.board.domain.user.model.PwSecLevel;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.service.help.UserHelpService;
import sky.board.domain.user.service.log.UserActivityLogService;
import sky.board.domain.user.service.UserQueryService;
import sky.board.domain.user.service.login.UserLoginStatusService;
import sky.board.domain.user.utili.CustomCookie;
import sky.board.domain.user.utili.PwChecker;
import sky.board.global.error.dto.FieldErrorCustom;
import sky.board.global.openapi.service.ApiExamCaptchaNkeyService;
import sky.board.global.utili.Alert;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/user/help")
public class HelpController {

    private final UserQueryService userQueryService;

    private final UserLoginStatusService userLoginStatusService;
    private final UserActivityLogService userActivityLogService;

    private final UserHelpService userHelpService;
    private final MessageSource ms;

    private final ApiExamCaptchaNkeyService apiExamCaptchaNkeyService;


    /**
     * @param model
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/id")
    public String idHelpForm(Model model, HttpServletRequest request, HttpServletResponse response) {

        UserHelpDto userHelpDto = UserHelpDto.createUserHelpIdDto();
        return getString(userHelpDto, model, HelpType.ID, "id", "helpForm", request, response);
    }

    @GetMapping("/idquery/check")
    public String idQuery(@ModelAttribute UserIdHelpQueryDto userIdHelpQueryDto, Model model,
        HttpServletRequest request,
        HttpServletResponse response)
        throws IOException {
        if (!StringUtils.hasText(userIdHelpQueryDto.getUserId())) {
            Alert.waringAlert(ms.getMessage("NotBlank", null, request.getLocale()), "/user/help/idquery", response);
        }

        try {
            CustomUserDetails userDetails = userQueryService.findStatusUserId(userIdHelpQueryDto.getUserId(),
                Status.OFF);

            UserHelpDto userHelpDto = UserHelpDto.createUserHelpIdDto();
            userHelpDto.setUserId(userIdHelpQueryDto.getUserId());

            StringBuffer email = new StringBuffer(userDetails.getEmail());

            // 2221325@naver.com -> 22*****@n****.com
            anonymousEmail(email);
            userHelpDto.setEnEmail(email.toString());
            userHelpDto.setUserId(userIdHelpQueryDto.getUserId());

            HttpSession session = request.getSession();
            model.addAttribute("userHelpDto", userHelpDto);
            session.setAttribute("userHelpDto", userHelpDto);
            return getString(userHelpDto, model
                , HelpType.PW, "idquery", "helpForm", request, response);
        } catch (UsernameNotFoundException e) {
            Alert.waringAlert(ms.getMessage("userId.notfound", null, request.getLocale()), "/user/help/idquery",
                response);

            return "redirect:/user/help/idquery";
        }
    }

    @GetMapping("/idquery")
    public String idQueryForm(Model model, HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession();

        EmailAuthCodeDto emailAuthCode = (EmailAuthCodeDto) session.getAttribute("emailAuthCodeDto");

        Cookie helpToken = CustomCookie.getCookie(request.getCookies(), "helpToken");
        if (helpToken != null) {
            CustomCookie.delete(helpToken, response);
        }
        if (emailAuthCode != null) {
            session.removeAttribute("emailAuthCodeDto");
        }

        model.addAttribute("userIdHelpQueryDto", new UserIdHelpQueryDto());
        return "user/help/idQueryForm";
    }


    @GetMapping("/find")
    public String findJoinEmailById(@Validated @ModelAttribute UserHelpDto userHelpDto, BindingResult bindingResult,
        HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "user/help/helpForm";
        }

        String helpToken = CustomCookie.readCookie(request.getCookies(), "helpToken");
        HttpSession session = request.getSession();
        // helpToken 쿠키가 만료됐거나 , 쿠키에 저장된 토큰과 요청한 토큰이 안 맞을 경우
        if (!StringUtils.hasText(helpToken) || (!helpToken.equals(userHelpDto.getHelpToken()))) {
            bindingResult.reject("code.error");
            return "redirect:/user/help/id";
        }

        EmailAuthCodeDto emailAuthCodeDto = (EmailAuthCodeDto) session.getAttribute("emailAuthCodeDto");

        /**
         * 이메일 인증에 성공하지 못했으면
         */

        if (emailAuthCodeDto == null || !emailAuthCodeDto.getIsSuccess()) {
            session.removeAttribute("emailAuthCodeDto");
            bindingResult.addError(
                new FieldErrorCustom("userHelpDto",
                    "email", userHelpDto.getEmail(),
                    "userJoinForm.email2",
                    null));
            return "user/help/helpForm";
        }

        try {
            CustomUserDetails findOne = userQueryService.findByEmailOne(emailAuthCodeDto.getEmail());
            if (!findOne.isEnabled()) {
                throw new UsernameNotFoundException("email.notfound");
            }
            // 회원가입 날짜 전달
            userHelpDto.setUserId(findOne.getUserId());

            String url;

            // 비밀번호 찾기 일시
            if (userHelpDto.getHelpType().equals(HelpType.PW)) {
                redirectAttributes.addFlashAttribute("userHelpDto", userHelpDto);
                // 비밀번호 재설정 페이지 이동
                url = "redirect:/user/help/reset";
            } else {// 아이디 찾기 일시
                userHelpDto.setCreatedDateTime(findOne.getCreatedDateTime());
                redirectAttributes.addFlashAttribute("userHelpDto", userHelpDto);
                //helpToken 쿠키 삭제

                Cookie cookie = CustomCookie.getCookie(request.getCookies(), "helpToken");
                CustomCookie.delete(cookie, response);

                // 아이디 페이지 이동
                url = "redirect:/user/help/show";
            }

            return url;

        } catch (UsernameNotFoundException e) {
            bindingResult.addError(
                new FieldErrorCustom("userHelpDto",
                    "email", userHelpDto.getEmail(),
                    e.getMessage(),
                    null));
            return "redirect:/login";
        }
    }

    @GetMapping("/show")
    public String showId(@ModelAttribute UserHelpDto userHelpDto, HttpServletRequest request,
        HttpServletResponse response, Model model)
        throws IOException {
        if (!StringUtils.hasText(userHelpDto.getUserId())) {
            Alert.waringAlert(ms.getMessage("code.error", null, request.getLocale()), "/user/help/id", response);
        }
        model.addAttribute("userHelpDto", userHelpDto);
        return "user/help/showId";
    }

    @GetMapping("/reset")
    public String pwResetForm(@ModelAttribute UserHelpDto userHelpDto,
        HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
        Cookie helpToken = CustomCookie.getCookie(request.getCookies(), "helpToken");

        // helpToken 쿠키가 만료됐거나 , 쿠키에 저장된 토큰과 요청한 토큰이 안 맞을 경우
        if (!StringUtils.hasText(helpToken.getValue()) || (!helpToken.getValue().equals(userHelpDto.getHelpToken()))) {
            Alert.waringAlert(ms.getMessage("code.error", null, request.getLocale()), "/user/help/idquery", response);
        }
        //시간 900초 다시
        helpToken.setMaxAge(900);
        response.addCookie(helpToken);

        Map<String, Object> apiExamCaptchaNkey = apiExamCaptchaNkeyService.getApiExamCaptchaNkey();
        String key = (String) apiExamCaptchaNkey.get("key");
        String apiExamCaptchaImage = apiExamCaptchaNkeyService.getApiExamCaptchaImage(key);
        UserPwResetFormDto userPwResetFormDto = UserPwResetFormDto.builder()
            .userId(userHelpDto.getUserId())
            .helpToken(userHelpDto.getHelpToken())
            .captchaKey(key)
            .imageName(apiExamCaptchaImage).build();

        model.addAttribute("userPwResetFormDto", userPwResetFormDto);

        return "user/help/pwResetForm";
    }

    @PostMapping("/reset")
    public String pwReset(@Validated @ModelAttribute UserPwResetFormDto userPwResetFormDto, BindingResult bindingResult,
        HttpServletRequest request, Model model, HttpServletResponse response)
        throws IOException {
        if (bindingResult.hasErrors()) {
            Alert.waringAlert(ms.getMessage("code.error", null, request.getLocale()), null, response);
            return "user/help/pwResetForm";
        }

        boolean isCaptcha;
        Map result = apiExamCaptchaNkeyService.getApiExamCaptchaNkeyResult(
            userPwResetFormDto.getImageName(),
            userPwResetFormDto.getCaptchaKey(), userPwResetFormDto.getCaptcha());

        isCaptcha = (boolean) result.get("result");

        // 자동입력 방지 번호가 맞지 않은 경우
        if (!isCaptcha) {
            bindingResult.addError(
                new FieldErrorCustom("userPwResetFormDto",
                    "captcha", null,
                    "error.captcha",
                    null));
        }
        // 확인 비밀번호가 불일치 할 경우
        if (!userPwResetFormDto.getNewPw().equals(userPwResetFormDto.getNewPwChk())) {
            bindingResult.addError(
                new FieldErrorCustom("userPwResetFormDto",
                    "newPwChk", userPwResetFormDto.getNewPw(),
                    "pw.mismatch",
                    null));
        }

        // 비밀번호 보안 레벨 확인
        PwSecLevel pwSecLevel = PwChecker.checkPw(userPwResetFormDto.getNewPw());
        // 비밀번호 값이 유효하지 않은 경우
        if (pwSecLevel.equals(PwSecLevel.NOT)) {
            bindingResult.addError(
                new FieldErrorCustom("userPwResetFormDto",
                    "newPw", userPwResetFormDto.getNewPw(),
                    "userJoinForm.password",
                    null));
        }

        if (bindingResult.hasErrors()) {
            apiExamCaptchaNkeyService.deleteImage(userPwResetFormDto.getImageName());
            setApiCaptcha(userPwResetFormDto);
            return "user/help/pwResetForm";
        }
        //
        if (StringUtils.hasText(userPwResetFormDto.getNewPw())) { // 비밀번호 재전송
            model.addAttribute("rePw", userPwResetFormDto.getNewPw());
        }

        // 보안레벨 저장 나중에 -> 보안 위험 표시할 떄 유용
        userPwResetFormDto.setPwSecLevel(pwSecLevel);
        try {
            CustomUserDetails userDetails = (CustomUserDetails) userHelpService.passwordUpdate(userPwResetFormDto);
            //변경로그
            userActivityLogService.save(userDetails.getUId(), userDetails.getUsername(), "sky.pw",
                "sky.log.pw.chaContent", request, ChangeSuccess.SUCCESS);
            // 비밀번호가 전과 같을시에 IllegalArgumentException

            HttpSession session = request.getSession(false);
            // 로그인된 기기 로그아웃

            userLoginStatusService.removeAllLoginStatus(userDetails.getUserId(),session.getId());

            //인증 이미지 삭제
            apiExamCaptchaNkeyService.deleteImage(userPwResetFormDto.getImageName());
            Alert.waringAlert(ms.getMessage("sky.newPw.success", null, request.getLocale()), "/logout", response);
            return null;
        } catch (IllegalArgumentException e) {
            setApiCaptcha(userPwResetFormDto);
            userActivityLogService.save(null, userPwResetFormDto.getUserId(), "sky.pw", "sky.log.pw.chaContent",
                request, ChangeSuccess.FAIL);
            bindingResult.addError(
                new FieldErrorCustom("userPwResetFormDto",
                    "newPw", userPwResetFormDto.getNewPw(),
                    e.getMessage(),
                    null));
            return "user/help/pwResetForm";
        }
    }







    private void setApiCaptcha(UserPwResetFormDto userPwResetFormDto) throws IOException {
        apiExamCaptchaNkeyService.deleteImage(userPwResetFormDto.getImageName());
        Map<String, Object> apiExamCaptchaNkey = apiExamCaptchaNkeyService.getApiExamCaptchaNkey();
        String key = (String) apiExamCaptchaNkey.get("key");
        String apiExamCaptchaImage = apiExamCaptchaNkeyService.getApiExamCaptchaImage(key);
        userPwResetFormDto.setCaptchaKey(key);
        userPwResetFormDto.setImageName(apiExamCaptchaImage);
    }

    private String getString(UserHelpDto userHelpDto, Model model, HelpType helpType, String url, String view,
        HttpServletRequest request,
        HttpServletResponse response) {

        // 뒤로가기 방지
        HttpSession session = request.getSession();
        EmailAuthCodeDto emailAuthCodeDto = (EmailAuthCodeDto) session.getAttribute("emailAuthCodeDto");

        Cookie result = CustomCookie.getCookie(request.getCookies(), "helpToken");

        if (result != null) {
            CustomCookie.delete(result, response);
            return "redirect:/user/help/" + url;
        }

        /**
         * 뒤로가기 방지
         */
        if (emailAuthCodeDto != null) {
            session.removeAttribute("emailAuthCodeDto");
            return "redirect:/user/help/" + url;
        }
        // 쿠키 생성
        CustomCookie.addCookie("/user/help", "helpToken", 900, response, userHelpDto.getHelpToken());

        userHelpDto.setHelpType(helpType);
        model.addAttribute("userHelpDto", userHelpDto);
        return "/user/help/" + view;
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

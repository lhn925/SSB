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
import sky.Sss.domain.email.dto.EmailAuthCodeDto;
import sky.Sss.domain.user.dto.help.UserHelpDto;
import sky.Sss.domain.user.dto.help.UserIdHelpQueryDto;
import sky.Sss.domain.user.dto.help.UserPwResetFormDto;
import sky.Sss.domain.user.dto.login.CustomUserDetails;
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
import sky.Sss.global.error.dto.FieldErrorCustom;
import sky.Sss.global.openapi.service.ApiExamCaptchaNkeyService;
import sky.Sss.global.utili.Alert;

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
     * id:help_1
     * <p>
     * id 찾기 페이지로 이동
     *
     * @param model
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/id")
    public String idHelpForm(Model model, HttpServletRequest request, HttpServletResponse response) {

        UserHelpDto userHelpDto = UserHelpDto.getInstance();
        return getForwardView(userHelpDto, model, HelpType.ID, "id", "helpForm", request, response
        );
    }


/*    @GetMapping("/helpForm")
    public String helpForm(@ModelAttribute UserHelpDto userHelpDto, Model model) {

        if (userHelpDto.getHelpType() == null) {
            return "redirect:/login";
        }
        model.addAttribute("userHelpDto", userHelpDto);
        return "user/help/helpForm";
    }*/

    /**
     * id:help_2
     * 이메일 인증 후
     * helpType 에 따라
     * PW : 비밀번호 재설정 페이지 호출 컨트롤러 이동
     * ID : 아이디 공개 페이지 호출 컨트롤러 이동
     *
     * @param userHelpDto
     * @param bindingResult
     * @param request
     * @param response
     * @param redirectAttributes
     * @return
     */
    @GetMapping("/find")
    public String findJoinEmailById(@Validated @ModelAttribute UserHelpDto userHelpDto, BindingResult bindingResult,
        HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "user/help/helpForm";
        }

        String helpToken = CustomCookie.readCookie(request.getCookies(), "helpToken");
        HttpSession session = request.getSession();

        UserHelpDto saveHelpDto = (UserHelpDto) session.getAttribute("userHelpDto");
        // helpToken 쿠키가 만료됐거나 , 쿠키에 저장된 토큰과 세션에 저장된 토큰이 안 맞을 경우
        if (!StringUtils.hasText(helpToken) || (!helpToken.equals(saveHelpDto.getHelpToken()))) {
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
            CustomUserDetails findOne = userQueryService.findByEmailOne(emailAuthCodeDto.getEmail(), Enabled.ENABLED);
            if (!findOne.isEnabled()) {
                throw new UsernameNotFoundException("email.notfound");
            }

            userHelpDto.setUserId(findOne.getUserId());

            String url;

            // 비밀번호 찾기 일시
            if (userHelpDto.getHelpType().equals(HelpType.PW)) {
                redirectAttributes.addFlashAttribute("userHelpDto", userHelpDto);
                // 비밀번호 재설정 페이지 이동
                url = "redirect:/user/help/reset";
            } else {
                // 아이디 찾기 일시

                // 회원가입 날짜 전달
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


    /**
     * id:help_3
     *
     * @param userHelpDto
     * @param request
     * @param response
     * @param model
     * @return
     * @throws IOException
     */
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

    /**
     * id:help_4
     * <p>
     * 유저가 찾을려는 아이디의 존재 유무를 확인 후
     * 확인 시 이메일 인증 페이지로 이동
     *
     * @param model
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/idquery")
    public String idQueryForm(Model model, HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession();

        EmailAuthCodeDto emailAuthCode = (EmailAuthCodeDto) session.getAttribute("emailAuthCodeDto");

        Cookie helpToken = CustomCookie.getCookie(request.getCookies(), "helpToken");
        if (helpToken != null) {
            CustomCookie.delete(helpToken, response);
        }
        if (emailAuthCode != null) {
            session.removeAttribute("helpToken");
            session.removeAttribute("emailAuthCodeDto");
        }

        model.addAttribute("userIdHelpQueryDto", new UserIdHelpQueryDto());
        return "user/help/idQueryForm";
    }


    /**
     * id:help_5
     * <p>
     * 비밀번호 찾기 시 아이디가 있는지 체크 후 email 인증 페이지로 이동
     *
     * @param userIdHelpQueryDto
     * @param model
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
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
                Enabled.ENABLED);


            // HelpToken 생성 및 세션 저장
            UserHelpDto userHelpDto = UserHelpDto.getInstance();
            userHelpDto.setUserId(userIdHelpQueryDto.getUserId());

            StringBuffer email = new StringBuffer(userDetails.getEmail());

            // 2221325@naver.com -> 22*****@n****.com
            anonymousEmail(email);
            userHelpDto.setEnEmail(email.toString());
            userHelpDto.setUserId(userIdHelpQueryDto.getUserId());

            return getForwardView(userHelpDto, model
                , HelpType.PW, "idquery", "helpForm", request, response);
        } catch (UsernameNotFoundException e) {
            Alert.waringAlert(ms.getMessage("userId.notfound", null, request.getLocale()), "/user/help/idquery",
                response);
            return "redirect:/user/help/idquery";
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return "redirect:/user/help/idquery";
        }
    }


    /**
     * id:help_6
     *  이메일 인증 후 비밀번호 재설정 페이지로 이동
     *
     * @param userHelpDto
     * @param request
     * @param response
     * @param model
     * @return
     * @throws IOException
     */
    @GetMapping("/reset")
    public String pwResetForm(@ModelAttribute UserHelpDto userHelpDto,
        HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
        Cookie helpToken = CustomCookie.getCookie(request.getCookies(), "helpToken");
        // helpToken 쿠키가 만료됐거나 , 쿠키에 저장된 토큰과 요청한 토큰이 안 맞을 경우

        UserHelpDto saveHelpDto = (UserHelpDto) request.getSession().getAttribute("userHelpDto");

        if (!StringUtils.hasText(helpToken.getValue()) || (!helpToken.getValue().equals(saveHelpDto.getHelpToken()))) {
            Alert.waringAlert(ms.getMessage("code.error", null, request.getLocale()), "/user/help/idquery", response);
        }
        if (userIdCheck(userHelpDto.getUserId(), request, response)) {
            return null;
        }

        //시간 900초 다시
        helpToken.setMaxAge(900);
        response.addCookie(helpToken);

        Map<String, Object> apiExamCaptchaNkey = apiExamCaptchaNkeyService.getApiExamCaptchaNkey();
        String key = (String) apiExamCaptchaNkey.get("key");
        String apiExamCaptchaImage = apiExamCaptchaNkeyService.getApiExamCaptchaImage(key);
        UserPwResetFormDto userPwResetFormDto = UserPwResetFormDto.builder()
            .userId(userHelpDto.getUserId())
            .captchaKey(key)
            .helpToken(userHelpDto.getHelpToken())
            .imageName(apiExamCaptchaImage).build();

        model.addAttribute("userPwResetFormDto", userPwResetFormDto);

        return "user/help/pwResetForm";
    }

    /**
     *  id:help_7
     *  비밀번호 재설정 후 성공 시 logout 페이지로 이동
     * @param userPwResetFormDto
     * @param bindingResult
     * @param request
     * @param model
     * @param response
     * @return
     * @throws IOException
     */
    @PostMapping("/reset")
    public String pwReset(@Validated @ModelAttribute UserPwResetFormDto userPwResetFormDto, BindingResult bindingResult,
        HttpServletRequest request, Model model, HttpServletResponse response)
        throws IOException {
        if (bindingResult.hasErrors()) {
            Alert.waringAlert(ms.getMessage("code.error", null, request.getLocale()), null, response);
            return "user/help/pwResetForm";
        }

        if (userIdCheck(userPwResetFormDto.getUserId(), request, response)) {
            return null;
        }
        boolean isCaptcha;
        Map result = apiExamCaptchaNkeyService.getApiExamCaptchaNkeyResult(
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

            HttpSession session = request.getSession();
            // 로그인된 기기 로그아웃

            userLoginStatusService.removeAllLoginStatus(userDetails.getUserId(), session.getId());

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

    private boolean userIdCheck(String userId, HttpServletRequest request,
        HttpServletResponse response) throws IOException {
        try {
            userQueryService.findOne(userId);
        } catch (IllegalArgumentException e) { //유저를 찾을 수 없는 경우
            Alert.waringAlert(ms.getMessage(e.getMessage(), null, request.getLocale()), "/", response);
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

    private String getForwardView(UserHelpDto userHelpDto, Model model, HelpType helpType, String url, String view,
        HttpServletRequest request,
        HttpServletResponse response) {

        // 뒤로가기 방지
        HttpSession session = request.getSession();
        EmailAuthCodeDto emailAuthCodeDto = (EmailAuthCodeDto) session.getAttribute("emailAuthCodeDto");

        Cookie result = CustomCookie.getCookie(request.getCookies(), "helpToken");

        if (result != null) {
            CustomCookie.delete(result, response);
            session.removeAttribute("userHelpDto");
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

        // 세션 저장
        session.setAttribute("userHelpDto", userHelpDto);
        model.addAttribute("userHelpDto", userHelpDto);
        return "user/help/" + view;

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

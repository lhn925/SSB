package sky.board.domain.user.controller;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sky.board.domain.email.dto.EmailAuthCodeDto;
import sky.board.domain.user.dto.help.UserHelpDto;
import sky.board.domain.user.dto.help.UserIdHelpQueryDto;
import sky.board.domain.user.dto.login.CustomUserDetails;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.service.UserQueryService;
import sky.board.domain.user.utili.CustomCookie;
import sky.board.global.error.dto.FieldErrorCustom;
import sky.board.global.utili.Alert;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/user/help")
public class HelpController {

    private final UserQueryService userQueryService;
    private final MessageSource ms;


    @GetMapping("/id")
    public String idHelpForm(Model model, HttpServletRequest request, HttpServletResponse response) {
        return getString(model, "id", "idHelpForm", request, response);
    }

    @GetMapping("/idquery/{userId}")
    public String idQuery(@PathVariable String userId, HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        if (!StringUtils.hasText(userId)) {
            Alert.waringAlert("NotBLank", "/user/help/inquery", response);
        }
        try {
            CustomUserDetails userDetails = userQueryService.findStatusUserId(userId, Status.OFF);

            UserHelpDto userHelpDto = UserHelpDto.createUserHelpIdDto();
            userHelpDto.setUserId(userId);

            String email = userDetails.getEmail();
            int indexFirst = email.indexOf("@");
            int indexLast = email.lastIndexOf(".");

            String subStr1 = email.substring(3, indexFirst - 1).replaceAll(".", "*");

            String subStr2 = email.substring(indexFirst + 2, indexLast).replaceAll(".", "*");

            userHelpDto.setEmail(userDetails.getEmail());


        } catch (UsernameNotFoundException e) {
            Alert.waringAlert(ms.getMessage("userId.notfound", null, request.getLocale()), "/user/help/idquery",
                response);
        }
    }

    @GetMapping("/idquery")
    public String idQueryForm(Model model) {
        model.addAttribute("userIdHelpQueryDto", new UserIdHelpQueryDto());
        return "user/help/idQueryForm";
    }


    @GetMapping("/find")
    public String findJoinEmailById(@Validated @ModelAttribute UserHelpDto userHelpDto, BindingResult bindingResult,
        HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "/user/help/idHelpForm";
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
            return "/user/help/idHelpForm";
        }

        try {
            CustomUserDetails findOne = userQueryService.findByEmailOne(emailAuthCodeDto.getEmail());
            if (!findOne.isEnabled()) {
                throw new UsernameNotFoundException("email.notfound");
            }
            // 회원가입 날짜 전달
            userHelpDto.setCreatedDateTime(findOne.getCreatedDateTime());
            userHelpDto.setUserId(findOne.getUserId());
            redirectAttributes.addFlashAttribute("userHelpDto", userHelpDto);

            //helpToken 쿠키 삭제
            Cookie cookie = CustomCookie.getCookie(request.getCookies(), "helpToken");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            return "redirect:/user/help/show";
        } catch (UsernameNotFoundException e) {
            bindingResult.addError(
                new FieldErrorCustom("userHelpDto",
                    "email", userHelpDto.getEmail(),
                    e.getMessage(),
                    null));
            return "redirect:/user/help/id";
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


    private String getString(Model model, String url, String view, HttpServletRequest request,
        HttpServletResponse response) {
        Cookie result = CustomCookie.getCookie(request.getCookies(), "helpToken");
        // 뒤로가기 방지
        HttpSession session = request.getSession();
        EmailAuthCodeDto emailAuthCodeDto = (EmailAuthCodeDto) session.getAttribute("emailAuthCodeDto");
        if (result != null) {
            result.setMaxAge(0);
            response.addCookie(result);
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
        UserHelpDto userHelpDto = UserHelpDto.createUserHelpIdDto();
        CustomCookie.addCookie("/user/help", "helpToken", 900, response, userHelpDto.getHelpToken());

        model.addAttribute("userHelpDto", userHelpDto);
        return "/user/help/" + view;
    }


}

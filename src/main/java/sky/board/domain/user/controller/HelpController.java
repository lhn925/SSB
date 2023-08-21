package sky.board.domain.user.controller;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
import sky.board.domain.email.dto.EmailAuthCodeDto;
import sky.board.domain.user.dto.help.UserHelpIdDto;
import sky.board.domain.user.dto.login.CustomUserDetails;
import sky.board.domain.user.service.UserQueryService;
import sky.board.domain.user.utill.CustomCookie;
import sky.board.global.error.dto.FieldErrorCustom;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/user/help")
public class HelpController {

    private final UserQueryService userQueryService;
    private final MessageSource ms;


    @GetMapping("/id")
    public String findIdHelpForm(Model model, HttpServletRequest request, HttpServletResponse response) {
        Cookie result = CustomCookie.getCookie(request.getCookies(), "helpToken");
        // 뒤로가기 방지
        HttpSession session = request.getSession();
        EmailAuthCodeDto emailAuthCodeDto = (EmailAuthCodeDto) session.getAttribute("emailAuthCodeDto");
        if (result != null) {
            result.setMaxAge(0);
            response.addCookie(result);
            return "redirect:/user/help/id";
        }
        /**
         * 뒤로가기 방지
         */
        if (emailAuthCodeDto != null) {
            session.removeAttribute("emailAuthCodeDto");
            return "redirect:/user/help/id";
        }
        // 쿠키 생성
        UserHelpIdDto userHelpIdDto = UserHelpIdDto.createUserHelpIdDto();
        CustomCookie.addCookie("/user/help", "helpToken", 900, response, userHelpIdDto.getHelpToken());

        model.addAttribute("userHelpIdDto", userHelpIdDto);
        return "/user/help/idHelpForm";
    }

    @PostMapping("/find")
    public String findJoinIdList(@Validated @ModelAttribute UserHelpIdDto userHelpIdDto, BindingResult bindingResult,
        HttpServletRequest request, HttpServletResponse response,Model model) {
        if (bindingResult.hasErrors()) {
            return "/user/help/idHelpForm";
        }
        String helpToken = CustomCookie.readCookie(request.getCookies(), "helpToken");
        HttpSession session = request.getSession();

        // helpToken 쿠키가 만료됐거나 , 쿠키에 저장된 토큰과 요청한 토큰이 안 맞을 경우
        if (!StringUtils.hasText(helpToken) || (!helpToken.equals(userHelpIdDto.getHelpToken()))) {
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
                new FieldErrorCustom("userHelpIdDto",
                    "email", userHelpIdDto.getEmail(),
                    "userJoinForm.email2",
                    null));
            return "/user/help/idHelpForm";
        }

        try {
            CustomUserDetails findOne = userQueryService.findByEmailOne(userHelpIdDto.getEmail());
            if (!findOne.isEnabled()) {
                throw new UsernameNotFoundException("userName.notfound");
            }
            // 회원가입 날짜 전달
            userHelpIdDto.setCreatedDateTime(findOne.getCreatedDateTime());
            userHelpIdDto.setUserId(findOne.getUserId());
            model.addAttribute("userHelpIdDto", userHelpIdDto);

            //helpToken 쿠키 삭제
            Cookie cookie = CustomCookie.getCookie(request.getCookies(), "helpToken");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            return "user/help/findId";
        } catch (UsernameNotFoundException e) {
            bindingResult.addError(
                new FieldErrorCustom("userHelpIdDto",
                    "email", userHelpIdDto.getEmail(),
                    e.getMessage(),
                    null));
            return "/user/help/idHelpForm";
        }
    }


}

package sky.board.domain.user.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sky.board.domain.user.dto.UserLoginFailErrorDto;
import sky.board.domain.user.dto.UserLoginFormDto;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {

//https://nid.naver.com/nidlogin.login?mode=form&url=https://www.naver.com/


    private final MessageSource ms;

    /**
     * @param request
     * @return
     */
    @GetMapping
    public String loginForm(@ModelAttribute UserLoginFormDto userLoginFormDto,
        @ModelAttribute UserLoginFailErrorDto userLoginFailErrorDto,
        HttpServletRequest request, Model model) {
        // error 또는 retryTwoFactor가 true 일 경우
        if (userLoginFailErrorDto.isError() || userLoginFailErrorDto.isRetryTwoFactor()) {
            model.addAttribute("errMsg", ms.getMessage(userLoginFailErrorDto.getErrMsg(),
                null, request.getLocale()));
        }

        model.addAttribute("userLoginFormDto", userLoginFormDto);
        return "/user/login";
    }

    /**
     * 주소값에 파라미터가 노출되지 않게끔
     */
    @GetMapping("/fail")
    public String failLoginForm(@ModelAttribute UserLoginFormDto userLoginFormDto,
        @ModelAttribute UserLoginFailErrorDto userLoginFailErrorDto,
        RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("userLoginFormDto", userLoginFormDto);
        redirectAttributes.addFlashAttribute("userLoginFailErrorDto", userLoginFailErrorDto);
        return "redirect:/login";
    }

    /**
     * 로그인 성공 후 호출되는 API
     * 0dksmf071
     * 0dlagksmf2
     *
     */


}

package sky.Sss.domain.user.controller;


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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sky.Sss.domain.user.dto.login.UserLoginFailErrorDto;
import sky.Sss.domain.user.dto.login.UserLoginFormDto;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {

    private final MessageSource ms;

    /**
     * @param request
     * @return
     */

    /**
     *
     * id:login_1
     *
     * 로그인페이지 이동
     * @param userLoginFormDto
     * @param userLoginFailErrorDto
     * @param request
     * @param model
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

        // 이전페이지 url 저장
        String referer = request.getHeader("referer");
        if (StringUtils.hasText(referer) && !StringUtils.hasText(userLoginFormDto.getUrl()) && !referer.contains("/login") ) {
            String url = referer.equals(request.getRequestURL()) ? null : referer;
            userLoginFormDto.setUrl(url);
        }

        model.addAttribute("userLoginFormDto", userLoginFormDto);
        return "user/login/loginForm";
    }

    /**
     * 주소값에 파라미터가 노출되지 않게끔
     * id:login_2
     * @param userLoginFormDto
     * @param userLoginFailErrorDto
     * @param redirectAttributes
     * @return
     */
    @GetMapping("/fail")
    public String failLoginForm(@ModelAttribute UserLoginFormDto userLoginFormDto,
        @ModelAttribute UserLoginFailErrorDto userLoginFailErrorDto,
        RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("userLoginFormDto", userLoginFormDto);
        redirectAttributes.addFlashAttribute("userLoginFailErrorDto", userLoginFailErrorDto);
        return "redirect:/login";
    }

}

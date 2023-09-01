package sky.board.domain.user.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sky.board.domain.user.dto.login.UserLoginFailErrorDto;
import sky.board.domain.user.dto.login.UserLoginFormDto;
import sky.board.global.redis.dto.RedisKeyDto;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {

    private final MessageSource ms;
    private final RedisTemplate redisTemplate;

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

        // 이전페이지 url 저장
        String referer = request.getHeader("referer");
        if (StringUtils.hasText(referer) && !StringUtils.hasText(userLoginFormDto.getUrl()) ) {
            String url = referer.equals(request.getRequestURL()) ? null : referer;
            userLoginFormDto.setUrl(url);
        }

        model.addAttribute("userLoginFormDto", userLoginFormDto);
        return "/user/login/login";
    }

    /**
     * 주소값에 파라미터가 노출되지 않게끔
     */
    @GetMapping("/fail")
    public String failLoginForm(@ModelAttribute UserLoginFormDto userLoginFormDto,
        @ModelAttribute UserLoginFailErrorDto userLoginFailErrorDto,
        RedirectAttributes redirectAttributes) {

        log.info("userLoginFormDto.getUserId() = {}", userLoginFormDto.getUserId());
        redirectAttributes.addFlashAttribute("userLoginFormDto", userLoginFormDto);
        redirectAttributes.addFlashAttribute("userLoginFailErrorDto", userLoginFailErrorDto);
        log.info("userLoginFormDto.getUserId()2 = {}", userLoginFormDto.getUserId());
        return "redirect:/login";
    }

/*    *//**
     * 로그인 성공 후 호출되는 API
     * 0dksmf071
     * 0dlagksmf2
     *//*
    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        Object user_id = httpSession.getAttribute(RedisKeyDto.USER_KEY);
        if (user_id != null) {
            httpSession.removeAttribute(RedisKeyDto.USER_KEY);
        }
        log.info("RedisKeyDto.SESSION_KEY = {}", RedisKeyDto.SESSION_KEY);
        String id = httpSession.getId();
        if (StringUtils.hasText(id)) {
            redisTemplate.delete(RedisKeyDto.SESSION_KEY + httpSession.getId());
        }
        return "redirect:/";
    }*/
}

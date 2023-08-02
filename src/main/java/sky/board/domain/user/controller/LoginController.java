package sky.board.domain.user.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sky.board.domain.user.dto.UserLoginReqDto;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {

    @GetMapping
    public String loginForm(@RequestParam(defaultValue = "/", required = false) String url, HttpServletRequest request) {
        request.setAttribute("url", url);
        return "/user/login";
    }


    /**
     * 로그인 성공 후 호출되는 API
     * 0dksmf071
     * 0dlagksmf2
     *
     * @param user
     * @return
     */
    @GetMapping("/dashboard")
    public String dashBoardPage(@AuthenticationPrincipal UserDetails user, HttpServletRequest request) {

        String password = request.getHeader("password");
        String url = (String) request.getSession().getAttribute("url");
        log.info("user url {}", url);
        log.info("getHeader password {}",password);
        return "redirect:/";
    }


}

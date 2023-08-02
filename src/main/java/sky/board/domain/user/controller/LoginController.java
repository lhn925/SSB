package sky.board.domain.user.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {

    @GetMapping
    public String loginForm(@RequestParam(defaultValue = "/", required = false) String url,
        HttpServletRequest request) {
        request.setAttribute("url",url);
        return "/user/login";
    }


    /**
     * 로그인 성공 후 호출되는 API
     * 0dksmf071
     * 0dlagksmf2
     *
     */


}

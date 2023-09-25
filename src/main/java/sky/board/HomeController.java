package sky.board;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sky.board.domain.user.dto.login.CustomUserDetails;
import sky.board.global.redis.service.RedisService;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {


    private final RedisService redisService;

    @GetMapping("/")
    public String home(HttpServletRequest request) {


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();




        log.info("authentication  = {}", authentication);
        log.info("authentication.getPrincipal() = {}", authentication.getPrincipal());
        log.info("authentication.getPrincipal().getClass() = {}", authentication.getPrincipal().getClass());
        log.info("authentication.getDetails()", authentication.getDetails());
        log.info("authentication.getAuthorities() = {}", authentication.getAuthorities().stream().toList().get(0));
        log.info("authentication.isAuthenticated() = {}", authentication.isAuthenticated());
        log.info("authentication = {}", authentication.getDetails());

//        Optional<String> ss_id = CustomCookie.readCookie(cookies, "SS_ID");

//        log.info("ss_id = {}", ss_id);


        return "home";
    }



    /**
     * 로그인 성공 후 호출되는 API
     * 0dksmf071
     * 0dlagksmf2
     * @param user
     * @param model
     * @return
     */
    @GetMapping("/view/dashboard")
    public String dashBoardPage(@AuthenticationPrincipal UserDetails user, Model model, HttpServletRequest request){
        return "redirect:/";
    }



}

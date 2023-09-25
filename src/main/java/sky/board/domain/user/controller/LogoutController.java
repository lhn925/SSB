package sky.board.domain.user.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sky.board.domain.user.utili.handler.logout.CustomSimpleUrlLogoutSuccessHandler;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class LogoutController {


    SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
    @PostMapping("/logout")
    public void logout (Authentication authentication,HttpServletRequest request, HttpServletResponse response) {
        log.info("LogoutController.getDetails() = {}", authentication.getDetails());
        logoutHandler.logout(request,response,authentication);
    }

}

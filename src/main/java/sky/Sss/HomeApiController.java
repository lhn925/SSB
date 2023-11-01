package sky.Sss;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.global.redis.service.RedisService;

@RestController("/home")
@Slf4j
@RequiredArgsConstructor
public class HomeApiController {


    private final RedisService redisService;

    @GetMapping
    public String home() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("authentication  = {}", authentication);
        log.info("authentication.getPrincipal() = {}", authentication.getPrincipal());
        log.info("authentication.getPrincipal().getClass() = {}", authentication.getPrincipal().getClass());
        log.info("authentication.getDetails()", authentication.getDetails());
        log.info("authentication.getAuthorities() = {}", authentication.getAuthorities().stream().toList().get(0));
        log.info("authentication.isAuthenticated() = {}", authentication.isAuthenticated());
        log.info("authentication = {}", authentication.getDetails());

        return "index";
    }

}

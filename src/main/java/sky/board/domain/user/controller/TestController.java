package sky.board.domain.user.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    private final RedisTemplate redisTemplate;

    @GetMapping("/test")
    public String getSession(HttpSession httpSession,HttpSessionRequestCache httpSessionRequestCache) {
        log.info("httpSession.getId() = {}", httpSession.getId());
        log.info("httpSession.getCreationTime() = {}", new Date(httpSession.getCreationTime()));
        log.info("httpSession.getLastAccessedTime() = {}",new Date(httpSession.getLastAccessedTime()));
        log.info("httpSession = {}", httpSession.getMaxInactiveInterval() / 60);

        Iterator<String> stringIterator = httpSession.getAttributeNames().asIterator();
        for (Iterator<String> it = stringIterator; it.hasNext(); ) {
            String attributeName = it.next();
            log.info("attributeName = {}", attributeName);
        }


        return httpSession.toString();
    }

    @GetMapping("/test/logout")
    public String logout(HttpSession httpSession) {
        Object user_id = httpSession.getAttribute("USER_ID");
        if (user_id != null) {
            httpSession.removeAttribute("USER_ID");
        }
        redisTemplate.delete("spring:session:Erjuer:sessions:"+httpSession.getId());
        return httpSession.getId();
    }
}

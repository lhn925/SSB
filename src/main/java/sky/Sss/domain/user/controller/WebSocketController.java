package sky.Sss.domain.user.controller;


import jakarta.servlet.http.HttpSession;
import java.util.Date;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.WebSocketSession;
import sky.Sss.domain.user.dto.Greeting;
import sky.Sss.domain.user.dto.HelloMessage;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public ResponseEntity greeting(HelloMessage message) throws Exception {
        log.info("message = {}", message);

        return new ResponseEntity(new Greeting("안녕하세요 " + message.getName()), HttpStatus.OK);
    }

    @MessageMapping("/login")
    @SendTo("/topic/greetings")
    public ResponseEntity login(SimpMessageHeaderAccessor headerAccessor ) throws Exception {


        return null;
    }
}


package sky.Sss.domain.user.controller;


import java.net.SocketException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.user.dto.LoginWebSocketDto;
import sky.Sss.domain.user.service.login.UserLoginStatusService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final UserLoginStatusService userLoginStatusService;


    @MessageMapping("/login")
    @SendToUser("/queue/alarm")
    public LoginWebSocketDto login(SimpMessageHeaderAccessor headerAccessor,final LoginWebSocketDto message) throws Exception {

        log.info("WebSocketController.ge = {}");
        String uuid = headerAccessor.getSessionId();
        message.setData("/queue/alarm/" + uuid);
        messagingTemplate.convertAndSendToUser(uuid,"/queue/alarm/"+uuid,message);
        return message;
    }

    @MessageMapping("/logout")
    @SendToUser("/queue/logout")
    public LoginWebSocketDto logout(SimpMessageHeaderAccessor headerAccessor,final LoginWebSocketDto message) throws Exception {
        String uuid = headerAccessor.getSessionId();
        message.setData("/queue/logout/" + message.getSessionId());


        messagingTemplate.convertAndSendToUser(uuid,"/queue/alarm/"+uuid,message);
        return message;
    }

    // OrderController.java class
    @MessageExceptionHandler // message 처리
    @SendToUser("/queue/errors") // '/user/queue/errors' 를 구독하고 있으면 보낸 사람에게만 ErrorDto가 전달된다.
    public String handleException(SocketException exception) {
        // throw new SocketException('message custom')을 던지면 이리로 들어온다.
        log.warn(exception.getMessage());
        return "안녕";
    }

}


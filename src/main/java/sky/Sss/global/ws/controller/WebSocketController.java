package sky.Sss.global.ws.controller;


import java.net.SocketException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.global.ws.dto.LoginWebSocketDto;
import sky.Sss.global.ws.dto.TestWebSocketDto;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * @MessageMapping: 클라이언트에서 서버로 보낸 메시지를 메시지를 라우팅
     * @SendTo: 구독한 클라이언트에게 response를 제공할 url 정의
     * @DestinationVariable: 구독 및 메시징의 동적 url 변수를 설정. RestAPI의 @PathValue와 같다.
     * @Payload: 메시지의 body를 정의한 객체에 매핑합니다.
     */




    @MessageMapping("/login")
    @SendToUser("/queue/alarm")
    public LoginWebSocketDto login(SimpMessageHeaderAccessor headerAccessor,LoginWebSocketDto message) throws Exception {
        log.info("WebSocketController.ge = {}");
        String uuid = headerAccessor.getSessionId();
        message.setData("/queue/alarm/" + uuid);
        messagingTemplate.convertAndSendToUser(uuid,"/queue/alarm/"+uuid,message);
        return message;
    }

    @MessageMapping("/push")
    @SendToUser("/queue/push/msg/{userId}")
    public TestWebSocketDto push(SimpMessageHeaderAccessor headerAccessor,@Payload TestWebSocketDto message) throws Exception {
        log.info("push ebSocketController.ge = {}",message.getUserId());
        log.info("push headerAccessor.getSessionId() = {}", headerAccessor.getSessionId());
        log.info("push message = {}", message.getMessage());
        String uuid = headerAccessor.getSessionId();
        log.info("uuid = {}", uuid);
//        message.setData("/queue/push/msg" + uuid);
//        messagingTemplate.convertAndSendToUser(uuid,"/queue/push/msg/"+uuid,message);
        return message;
    }

    @MessageMapping("/push/sub")
    @SendTo("/topic/push/{userId}")
    public TestWebSocketDto pushSub(SimpMessageHeaderAccessor headerAccessor,@Payload TestWebSocketDto message) throws Exception {
        log.info("pushSub ebSocketController.ge = {}",message.getUserId());
        log.info("pushSub headerAccessor.getSessionId() = {}", headerAccessor.getSessionId());
        log.info("pushSub message = {}", message.getMessage());
        String uuid = headerAccessor.getSessionId();
        log.info("pushSub = {}", uuid);
//        message.setData("/queue/push/msg" + uuid);
//        messagingTemplate.convertAndSendToUser(uuid,"/queue/push/msg/"+uuid,message);
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
    @SendToUser("/queue/errors") // '/users/queue/errors' 를 구독하고 있으면 보낸 사람에게만 ErrorDto가 전달된다.
    public String handleException(SocketException exception) {
        // throw new SocketException('message custom')을 던지면 이리로 들어온다.
        log.warn(exception.getMessage());
        return "안녕";
    }

}


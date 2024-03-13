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
import sky.Sss.domain.user.dto.push.PushMsgDto;
import sky.Sss.global.ws.dto.LogOutWebSocketDto;
import sky.Sss.global.ws.dto.LoginWebSocketDto;
import sky.Sss.global.ws.dto.PushWebSocketDto;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WebSocketController {

    /**
     * @MessageMapping: 클라이언트에서 서버로 보낸 메시지를 메시지를 라우팅
     * @SendTo: 구독한 클라이언트에게 response를 제공할 url 정의
     * @DestinationVariable: 구독 및 메시징의 동적 url 변수를 설정. RestAPI의 @PathValue와 같다.
     * @Payload: 메시지의 body를 정의한 객체에 매핑합니다.
     */

    @MessageMapping("/push")
    @SendTo("/topic/push/{userId}")
    public PushMsgDto pushSub(SimpMessageHeaderAccessor headerAccessor,@Payload PushMsgDto message) throws Exception {

        return message;
    }

    @MessageMapping("/logout")
    @SendTo("/topic/logout/{token}")
    public LogOutWebSocketDto logout(SimpMessageHeaderAccessor headerAccessor,@Payload LogOutWebSocketDto message) throws Exception {
        return message;
    }

}


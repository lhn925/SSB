package sky.Sss.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class MsgTemplateService {

    private final SimpMessagingTemplate messagingTemplate;

    public void convertAndSend(String url, Object payload) {
        messagingTemplate.convertAndSend(url, payload);
    }

    public void convertAndSendUser(String user, String url,Object payload) {
        messagingTemplate.convertAndSendToUser(user, url,payload);
    }


}

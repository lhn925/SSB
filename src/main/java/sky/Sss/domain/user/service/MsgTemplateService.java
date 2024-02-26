package sky.Sss.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import sky.Sss.domain.user.dto.PushMsgDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.UserPushMessages;
import sky.Sss.global.redis.service.RedisCacheService;

@RequiredArgsConstructor
@Slf4j
@Service
public class MsgTemplateService {

    private final SimpMessagingTemplate messagingTemplate;

    public void convertAndSend(String url, Object payload) {
        messagingTemplate.convertAndSend(url, payload);
    }



}

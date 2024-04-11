package sky.Sss.domain.user.service;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.service.track.TrackLikesService;
import sky.Sss.domain.track.service.track.TrackQueryService;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.repository.push.UserPushMsgRepository;
import sky.Sss.domain.user.service.push.UserPushMsgService;
import sky.Sss.global.ws.dto.PushWebSocketDto;

@SpringBootTest
class UserPushMsgServiceTest {


    @Autowired
    UserQueryService userQueryService;

    @Autowired
    TrackQueryService trackQueryService;

    @Autowired
    TrackLikesService trackLikesService;

    @Autowired
    UserPushMsgService userPushMsgService;


    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @Autowired
    UserPushMsgRepository userPushMsgRepository;




    @Test
    void addUserPushMsg() {
        User fromUser = userQueryService.findOne("0221325");
        SsbTrack ssbTrack = trackQueryService.findOneJoinUser(54L, Status.ON);
//        SsbTrackLikes ssbTrackLikes = SsbTrackLikes.create(fromUser, ssbTrack);
//        trackLikesService.addLike(ssbTrackLikes);

//        UserPushMessages userPushMessages = UserPushMessages.create(ssbTrack.getUser(), fromUser, PushMsgType.LIKES,
//            ContentsType.TRACK);
//        userPushMsgRepository.save(userPushMessages);
    }


    @Test
    public void messageTemplate() {
        User fromUser = userQueryService.findOne("lim222");
        String sessionId = "618b5c69-c281-3633-8131-8dad527b5a45";
        PushWebSocketDto pushWebSocketDto = new PushWebSocketDto("lim222", "안녕하세요",sessionId);

        messagingTemplate.convertAndSend("/topic/push/lim222", pushWebSocketDto);
    // given

    // when

    // then

    }
}
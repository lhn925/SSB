package sky.Sss.domain.track.service.track.reply;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.Sss.domain.track.entity.track.reply.SsbTrackReply;
import sky.Sss.domain.track.repository.track.reply.TrackReplyRepository;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.repository.UserQueryRepository;


@SpringBootTest
class TrackTrackCommonServiceTest {


    @Autowired
    TrackReplyRepository trackReplyRepository;


    @Autowired
    UserQueryRepository userQueryRepository;

    @Test
    void findByJoinUserAndTrack() {
        SsbTrackReply ssbTrackReply = trackReplyRepository.findOne(1L).get();

        User user = userQueryRepository.findById(1L).get();

        System.out.println("user.getId() = " + user.getId());

        System.out.println("ssbTrackReply.getUser().getId() = " + ssbTrackReply.getUser().getId());

        System.out.println("(user == ssbTrackReply.getUser()) = " + (user == ssbTrackReply.getUser()));

        System.out.println("user.equals(ssbTrackReply.getUser()) = " + user.equals(ssbTrackReply.getUser()));
    }
}
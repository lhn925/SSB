package sky.Sss.domain.track.repository.common;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.handler.annotation.Payload;
import sky.Sss.domain.track.dto.common.repost.RepostSimpleInfoDto;
import sky.Sss.domain.user.model.ContentsType;


@SpringBootTest
class RepostTest {


    @Autowired
    RepostRepository repostRepository;




    @Test
    public void getRepostSimpleDtoJoinTrack () {
        String token = "ebb90c922de3f3082dc8";

        List<RepostSimpleInfoDto> repostSimpleDtoJoinTrack = repostRepository.getRepostSimpleDtoJoinTrack(token,
            ContentsType.TRACK);

        System.out.println("repostSimpleDtoJoinTrack.size() = " + repostSimpleDtoJoinTrack.size());



    }






}
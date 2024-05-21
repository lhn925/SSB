package sky.Sss.domain.track.service.playList;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.service.track.TrackLikesService;
import sky.Sss.domain.track.service.track.TrackQueryService;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Status;


@SpringBootTest
class PlyLikesServiceTest {


    @Autowired
    PlyLikesService plyLikesService;

    @Autowired
    TrackLikesService trackLikesService;


    @Autowired
    TrackQueryService trackQueryService;

    @Test
    public void getLikeTotal() {
        SsbTrack byId = trackQueryService.findById(1L, Status.ON);

        int totalCount = trackLikesService.getTotalCount(byId.getToken());

        System.out.println("totalCount = " + totalCount);


    }
    @Test
    public void getUserList() {
        SsbTrack byId = trackQueryService.findById(2L, Status.ON);
        List<User> userList = trackLikesService.getUserList(byId.getToken());

        for (User user : userList) {
            System.out.println("user.getToken() = " + user.getToken());
        }


    }



}
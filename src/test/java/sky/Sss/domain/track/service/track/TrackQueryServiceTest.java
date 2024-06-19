package sky.Sss.domain.track.service.track;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.Sss.domain.track.dto.track.rep.TrackUploadCountDto;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.service.common.LikesCommonService;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.ContentsType;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.UserQueryService;

@SpringBootTest
class TrackQueryServiceTest {


    @Autowired
    TrackQueryService trackQueryService;


    @Autowired
    LikesCommonService likesCommonService;


    @Autowired
    UserQueryService userQueryService;

    @Test
    public void uploadTotalCount() {

        User user1 = userQueryService.findOne("1221325");

        List<User> users = new ArrayList<>();

        users.add(user1);
//        users.add(user3);

        TrackUploadCountDto userUploadCount = trackQueryService.getMyUploadCount(user1, Status.ON);

        System.out.println("userUploadCount.getUid() = " + userUploadCount.getUid());
        System.out.println("userUploadCount.getTotalCount() = " + userUploadCount.getTotalCount());
    }

    @Test
    public void getUsersUploadCount() {

        User user1 = userQueryService.findOne("0221325");
        User user2 = userQueryService.findOne("1221325");
        User user3 = userQueryService.findOne("lim222");

        List<User> users = new ArrayList<>();

        users.add(user1);
        users.add(user2);
        users.add(user3);

        Map<String, TrackUploadCountDto> usersUploadCount = trackQueryService.getUsersUploadCount(users, Status.ON);

        for (String s : usersUploadCount.keySet()) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void fetchTest() {

        Set<Long> ids = new HashSet<>();

        ids.add(1L);
        ids.add(2L);
        ids.add(3L);

        List<SsbTrack> trackListFromOrDbByIds = trackQueryService.getTrackListFromOrDbByIds(ids);

        for (SsbTrack trackListFromOrDbById : trackListFromOrDbByIds) {

            int totalCount = likesCommonService.getTotalCount(trackListFromOrDbById.getToken(), ContentsType.TRACK);
            System.out.println("trackListFromOrDbById.getTitle() = " + trackListFromOrDbById.getTitle());
            System.out.println("totalCount = " + totalCount);

        }
    }

}
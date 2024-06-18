package sky.Sss.domain.track.service.track;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

        User user = userQueryService.findOne("lim2226");

        int userUploadCount = trackQueryService.getUserUploadCount(user, Status.ON);

        System.out.println("userUploadCount = " + userUploadCount);


        int myUploadCount = trackQueryService.getMyUploadCount(user, Status.ON);

        System.out.println("myUploadCount = " + myUploadCount);


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
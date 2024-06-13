package sky.Sss.domain.track.service.track;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.Sss.domain.track.dto.track.rep.TrackSearchInfoDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Enabled;
import sky.Sss.domain.user.service.UserQueryService;


@SpringBootTest
class TrackInfoServiceTest {

    @Autowired
    TrackInfoService trackInfoService;

    @Autowired
    UserQueryService userQueryService;

    @Test
    public void getTrackSearchInfoList() {

        User user = userQueryService.findOne(4L, Enabled.ENABLED);
        Set<Long> ids = new HashSet<>();
        // given
        ids.add(1L);
        ids.add(4L);
        ids.add(5L);
        ids.add(66L);
        ids.add(61L);
        ids.add(63L);
        ids.add(6512L);
        ids.add(623L);
        ids.add(62354L);
        ids.add(622L);
        ids.add(7L);
        ids.add(12L);
        ids.add(10L);
        ids.add(222L);

        // when
        List<TrackSearchInfoDto> trackSearchInfoList = trackInfoService.getTrackSearchInfoList(ids, user);
        // then

        for (TrackSearchInfoDto trackSearchInfoDto : trackSearchInfoList) {
            System.out.println(" ===================== ===================== =====================");
            System.out.println("trackSearchInfoDto.getTrackInfo().getId() = " + trackSearchInfoDto.getTrackInfo().getId());
            System.out.println("trackSearchInfoDto.getTrackInfo().getToken() = " + trackSearchInfoDto.getTrackInfo().getToken());
            System.out.println("trackSearchInfoDto.getTrackInfo().getTitle() = " + trackSearchInfoDto.getTrackInfo().getTitle());
            System.out.println("trackSearchInfoDto.getPlayCount() = " + trackSearchInfoDto.getPlayCount());
            System.out.println("trackSearchInfoDto.getLikeCount() = " + trackSearchInfoDto.getLikeCount());
            System.out.println("trackSearchInfoDto.getReplyCount() = " + trackSearchInfoDto.getReplyCount());
            System.out.println("trackSearchInfoDto.getRepostCount() = " + trackSearchInfoDto.getRepostCount());
        }

    }

}
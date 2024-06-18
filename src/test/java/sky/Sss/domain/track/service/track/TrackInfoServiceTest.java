package sky.Sss.domain.track.service.track;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.Sss.domain.track.dto.track.rep.TrackDetailDto;
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
        List<TrackDetailDto> trackSearchInfoList = trackInfoService.getTrackInfoList(ids, user);
        // then

        for (TrackDetailDto trackDetailDto : trackSearchInfoList) {
            System.out.println(" ===================== ===================== =====================");
            System.out.println("trackDetailDto.getTrackInfo().getId() = " + trackDetailDto.getTrackInfo().getId());
            System.out.println("trackDetailDto.getTrackInfo().getToken() = " + trackDetailDto.getTrackInfo().getToken());
            System.out.println("trackDetailDto.getTrackInfo().getTitle() = " + trackDetailDto.getTrackInfo().getTitle());
            System.out.println("trackDetailDto.getPlayCount() = " + trackDetailDto.getPlayCount());
            System.out.println("trackDetailDto.getLikeCount() = " + trackDetailDto.getLikeCount());
            System.out.println("trackDetailDto.getReplyCount() = " + trackDetailDto.getReplyCount());
            System.out.println("trackDetailDto.getRepostCount() = " + trackDetailDto.getRepostCount());
        }

    }

}
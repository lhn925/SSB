package sky.Sss.domain.user.service.profile;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.Sss.domain.track.dto.common.like.TrackTargetWithCountDto;
import sky.Sss.domain.track.dto.common.like.TrackTargetWithCountDto.targetInfo;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.service.UserQueryService;


@SpringBootTest
class UserProfileServiceTest {


    @Autowired
    UserQueryService userQueryService;


    @Autowired
    UserProfileService userProfileService;

    @Test
    public void getRecentLikedTracksWithCount() {

        // given
        User user = userQueryService.findOne("lim2226");

        // when
        TrackTargetWithCountDto recentLikedTracksWithCount = userProfileService.getRecentLikedTracksWithCount(user, user);

        // then

        List<targetInfo> likeInfos = recentLikedTracksWithCount.getTargetInfos();

        for (targetInfo likeInfo : likeInfos) {
            System.out.println("likeInfo.getId() = " + likeInfo.getId());
            System.out.println("likeInfo.getTrackInfo().getTrackInfo().getId() = " + likeInfo.getDetails().getTrackInfo().getId());
        }

        System.out.println("recentLikedTracksWithCount.getTotalCount() = " + recentLikedTracksWithCount.getTotalCount());

    }
}
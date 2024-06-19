package sky.Sss.domain.user.service.profile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.Sss.domain.track.dto.common.like.TrackTargetWithCountDto;
import sky.Sss.domain.track.dto.common.like.TrackTargetWithCountDto.targetInfo;
import sky.Sss.domain.user.dto.rep.UserProfileDto;
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



    @Test
    public void getProfileHeaderByUserName () {
        User user = userQueryService.findOne("lim2226");

        String userName = user.getUserName();

//        UserProfileDto headerByUserName = userProfileService.test("임하늘",user);
//
//        System.out.println("headerByUserName.getUid() = " + headerByUserName.getUid());
//        System.out.println("headerByUserName.getUserName() = " + headerByUserName.getUserName());
//        System.out.println("headerByUserName.getFollowerCount() = " + headerByUserName.getFollowerCount());
//        System.out.println("headerByUserName.getFollowingCount() = " + headerByUserName.getFollowingCount());
//
//        System.out.println("headerByUserName.getTrackTotalCount() = " + headerByUserName.getTrackTotalCount());
//        System.out.println("headerByUserName.getPictureUrl() = " + headerByUserName.getPictureUrl());

    }


    @Test
    public void getUserInfoListByIds() {

    // given

        Set<Long> ids = new HashSet<>();

        ids.add(1L);
        ids.add(2L);
        ids.add(3L);
        ids.add(4L);

        // when
        List<UserProfileDto> userInfoListByIds = userProfileService.getUserInfoListByIds(ids);

        // then

        for (UserProfileDto userInfoListById : userInfoListByIds) {

            System.out.println(" ==================== ");
            System.out.println("userInfoListById.getUid() = " + userInfoListById.getUid());
            System.out.println("userInfoListById.getUserName() = " + userInfoListById.getUserName());
            System.out.println("userInfoListById.getPictureUrl() = " + userInfoListById.getPictureUrl());
            System.out.println("userInfoListById.getTrackTotalCount() = " + userInfoListById.getTrackTotalCount());
            System.out.println("userInfoListById =getFollowingCount = " + userInfoListById.getFollowingCount());
            System.out.println("userInfoListById =getFollowerCount = " + userInfoListById.getFollowerCount());
        }

    }

}
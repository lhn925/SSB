package sky.Sss.domain.user.service.profile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.Sss.domain.track.dto.common.like.TrackLikedWithCountDto;
import sky.Sss.domain.user.dto.follows.FollowsUserListDto;
import sky.Sss.domain.user.dto.redis.RedisFollowsDto;
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
        TrackLikedWithCountDto recentLikedTracksWithCount = userProfileService.getRecentLikedTracksWithCount(1L);

        // then
/*
        List<TargetInfo> likeInfos = recentLikedTracksWithCount.getTargetInfos();

        for (TargetInfo likeInfo : likeInfos) {
            System.out.println("likeInfo.getId() = " + likeInfo.getId());
            System.out.println(
                "likeInfo.getTrackInfo().getTrackInfo().getId() = " + likeInfo.getDetails().getTrackInfo().getId());
        }

        System.out.println(
            "recentLikedTracksWithCount.getTotalCount() = " + recentLikedTracksWithCount.getTotalCount());*/
    }


    @Test
    public void getProfileHeaderByUserName() {
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
    public void getRecentTop3FollowUser() {

        // given
        FollowsUserListDto recentTop3FollowUser = userProfileService.getRecentTop3FollowingUser(4L);

        List<RedisFollowsDto> followsInfoList = recentTop3FollowUser.getFollowsInfoList();

        Integer totalCount = recentTop3FollowUser.getTotalCount();

        for (RedisFollowsDto redisFollowsDto : followsInfoList) {
            System.out.println("redisFollowsDto.getId() = " + redisFollowsDto.getId());

            System.out.println("redisFollowsDto.getFollowingUid() = " + redisFollowsDto.getFollowingUid());

        }
        System.out.println("totalCount = " + totalCount);

    }

    @Test
    public void getFollowerUserList() {

        // given
        FollowsUserListDto followerUserList = userProfileService.getFollowerUserList(1L);

        List<RedisFollowsDto> followsInfoList = followerUserList.getFollowsInfoList();

        Integer totalCount = followerUserList.getTotalCount();

        System.out.println("totalCount = " + totalCount);

        for (RedisFollowsDto redisFollowsDto : followsInfoList) {
            System.out.println("redisFollowsDto.getFollowerUid() = " + redisFollowsDto.getFollowerUid());
        }
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
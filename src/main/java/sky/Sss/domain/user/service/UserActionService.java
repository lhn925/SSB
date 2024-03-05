package sky.Sss.domain.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.UserFollows;
import sky.Sss.domain.user.service.follows.UserFollowsService;
import sky.Sss.global.redis.dto.RedisKeyDto;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class UserActionService {

    private final UserFollowsService userFollowsService;



    @Transactional
    public void addFollows(User followerUser,User followingUser) {
        // 팔로우가 이미 되어 있는 지 확인
        boolean isFollows = userFollowsService.existsFollowing(followerUser, followingUser);
        if (isFollows) {
            throw new IllegalArgumentException();
        }
        UserFollows userFollows = UserFollows.create(followerUser, followingUser);
        // 팔로우 저장
        userFollowsService.addUserFollows(userFollows);



    }

    @Transactional
    public void cancelFollows(User followerUser,User followingUser) {
        // 팔로우가 이미 되어 있는 지 확인
        UserFollows userFollows = userFollowsService.findFollowingByFollowerUser(followingUser, followerUser);
        if (userFollows == null) {
            throw new IllegalArgumentException();
        }
        // 팔로우 저장
        userFollowsService.cancelFollow(userFollows);

    }


    public int getTotalFollowerCount(User user) {
        return userFollowsService.getFollowerTotalCount(user);
    }
    public int getTotalFollowingCount(User user) {
        return userFollowsService.getFollowingTotalCount(user);
    }


}

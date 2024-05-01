package sky.Sss.domain.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.UserFollows;
import sky.Sss.domain.user.entity.UserPushMessages;
import sky.Sss.domain.user.model.ContentsType;
import sky.Sss.domain.user.model.Enabled;
import sky.Sss.domain.user.model.PushMsgType;
import sky.Sss.domain.user.service.follows.UserFollowsService;
import sky.Sss.domain.user.service.push.UserPushMsgService;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class UserActionService {

    private final UserQueryService userQueryService;
    private final UserFollowsService userFollowsService;
    private final UserPushMsgService userPushMsgService;

    @Transactional
    public void addFollowerActions(Long followingUid) {
        // 팔로우를 신청한 사용자
        User fromUser = userQueryService.findOne();
        // 사용자가 같은경우 예외 처리
        checkFollowingUid(fromUser.getId().equals(followingUid));
        // 팔로우 대상자
        User toUser = userQueryService.findOne(followingUid, Enabled.ENABLED);

        addFollows(fromUser, toUser);

        UserPushMessages userPushMessages = UserPushMessages.create(toUser, fromUser, PushMsgType.FOLLOW,
            ContentsType.USER, null);

        // pushMsg
        userPushMsgService.addUserPushMsg(userPushMessages);
        userPushMsgService.sendOrCacheMessages(ContentsType.USER.getUrl() + fromUser.getId(), fromUser.getUserName(), toUser, userPushMessages);
        // 대상자에 follower count 검색 후 반환
    }

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


    private void checkFollowingUid(boolean followingUid) {
        if (followingUid) {
            throw new IllegalArgumentException();
        }
    }

}

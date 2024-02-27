package sky.Sss.domain.user.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.user.annotation.UserAuthorize;
import sky.Sss.domain.user.dto.FollowerTotalCountDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.UserPushMessages;
import sky.Sss.domain.user.model.ContentsType;
import sky.Sss.domain.user.model.Enabled;
import sky.Sss.domain.user.model.PushMsgType;
import sky.Sss.domain.user.service.PushMsgService;
import sky.Sss.domain.user.service.UserActionService;
import sky.Sss.domain.user.service.UserQueryService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/action")
@UserAuthorize
public class UserActionController {


    private final UserQueryService userQueryService;
    private final UserActionService userActionService;
    private final PushMsgService pushMsgService;

    /**
     * @param followingUid
     *     팔로우를 당하는 유저 ID
     * @return
     */
    // 팔로우
    @PostMapping("/my-following/{following-uid}")
    public ResponseEntity<FollowerTotalCountDto> saveFollowing(@PathVariable(name = "following-uid") Long followingUid) {
        checkFollowingUid(followingUid == null || followingUid <= 0);
        // 팔로우를 신청한 사용자
        User fromUser = userQueryService.findOne();

        // 사용자가 같은경우 예외 처리
        checkFollowingUid(fromUser.getId().equals(followingUid));
        // 팔로우 대상자
        User toUser = userQueryService.findOne(followingUid, Enabled.ENABLED);

        userActionService.addFollows(fromUser, toUser);

        UserPushMessages userPushMessages = UserPushMessages.create(toUser, fromUser, PushMsgType.FOLLOW,
            ContentsType.USER, null);

        // pushMsg
        pushMsgService.addUserPushMsg(userPushMessages);
        pushMsgService.sendOrCacheMessages(ContentsType.USER.getUrl() + fromUser.getId(), fromUser.getUserName(), toUser, userPushMessages);

        // 대상자에 follower count 검색 후 반환
        int totalFollowerCount = userActionService.getTotalFollowerCount(toUser);

        return ResponseEntity.ok(new FollowerTotalCountDto(totalFollowerCount));
    }

    // 상대방 언 팔로우
    /**
     * @param followingUid
     *     팔로우를 취소 당하는 유저 ID
     * @return FollowerTotalCountDto
     */
    // 팔로우
    @DeleteMapping("/my-following/{following-uid}")
    public ResponseEntity<FollowerTotalCountDto> removeFollowing(@PathVariable(name = "following-uid") Long followingUid) {
        // 값 확인
        checkFollowingUid(followingUid == null || followingUid <= 0);
        // 팔로우 취소를 신청한 사용자 검색
        User followerUser = userQueryService.findOne();
        // 사용자가 같은경우 예외 처리
        checkFollowingUid(followerUser.getId().equals(followingUid));
        // 언 팔로우 대상자
        User unFollowingUser = userQueryService.findOne(followingUid, Enabled.ENABLED);
        userActionService.cancelFollows(followerUser, unFollowingUser);

        // 대상자에 follower count 검색 후 반환
        int totalFollowerCount = userActionService.getTotalFollowerCount(unFollowingUser);

        return ResponseEntity.ok(new FollowerTotalCountDto(totalFollowerCount));
    }

    //나의 팔로우 리스트에서 나를 팔로우 하는 사용자를 강제 언팔
    /**
     * @param followerUid
     *     팔로우를 취소 당하는 유저 ID
     * @return FollowerTotalCountDto
     */
    // 팔로우
    @DeleteMapping("/my-followers/{follower-uid}")
    public ResponseEntity<FollowerTotalCountDto> removeFollowers(@PathVariable(name = "follower-uid") Long followerUid) {
        // 값 확인
        checkFollowingUid(followerUid == null || followerUid <= 0);
        // 팔로우 강제 언팔을 요청한 사용자 검색
        User followingUser = userQueryService.findOne();
        // 사용자가 같은경우 예외 처리
        checkFollowingUid(followingUser.getId().equals(followerUid));
        // 팔로우 삭제 대상자
        User followerUser = userQueryService.findOne(followerUid, Enabled.ENABLED);

        userActionService.cancelFollows(followerUser, followingUser);

        // 대상자에 follower count 검색 후 반환
        int totalFollowerCount = userActionService.getTotalFollowerCount(followingUser);

        return ResponseEntity.ok(new FollowerTotalCountDto(totalFollowerCount));
    }

    private void checkFollowingUid(boolean followingUid) {
        if (followingUid) {
            throw new IllegalArgumentException();
        }
    }



}

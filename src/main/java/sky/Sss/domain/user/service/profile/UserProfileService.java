package sky.Sss.domain.user.service.profile;


import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.common.like.LikedRedisDto;
import sky.Sss.domain.track.service.common.LikesCommonService;
import sky.Sss.domain.track.service.track.TrackLikesService;
import sky.Sss.domain.track.service.track.TrackQueryService;
import sky.Sss.domain.user.dto.myInfo.UserMyInfoDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.UserFollows;
import sky.Sss.domain.user.model.ContentsType;
import sky.Sss.domain.user.model.Enabled;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.domain.user.service.follows.UserFollowsService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileService {

    private final UserQueryService userQueryService;
    private final TrackQueryService trackQueryService;
    private final UserFollowsService userFollowsService;
    private final TrackLikesService trackLikesService;
    private final LikesCommonService likesCommonService;

    public UserMyInfoDto getUserMyInfoDto() {
        User user = userQueryService.findOne();

        List<LikedRedisDto> likeTrackIds = likesCommonService.getLikeTrackIds(user, ContentsType.TRACK);

        // 좋아하는 트랙리스트
        List<Long> userLikedList = likeTrackIds.stream().map(LikedRedisDto::getTargetId).toList();
        List<UserFollows> followingUsersFromCacheOrDB = userFollowsService.getFollowingUsersFromCacheOrDB(user);

        List<Long> followingIds = followingUsersFromCacheOrDB.stream().map(value -> value.getFollowerUser().getId())
            .toList();
        return new UserMyInfoDto(user.getUserId(), user.getEmail(), user.getUserName(), user.getPictureUrl(),
            user.getIsLoginBlocked(), user.getGrade(), userLikedList, followingIds);
    }

    public void getUserProfileByUserName(String userName) {
        // 본인 정보
        User user = userQueryService.findOne();

        User profileUser = userQueryService.findByUserName(userName, Enabled.ENABLED);

        // 본인 프로필 여부
        boolean isMyProfile = user.getToken().equals(profileUser.getToken());
        // 회원 닉네임
        // 회원 프로필 사진
        // 총 팔로워 수
        List<UserFollows> userFollowsList = userFollowsService.getFollowersUsersFromCacheOrDB(profileUser);
        int totalFollowerCount = userFollowsList.size();
        // 팔로잉 리스트
        List<UserFollows> userFollowingList = userFollowsService.getFollowingUsersFromCacheOrDB(profileUser);
        int totalFollowingCount = userFollowingList.size();

        Sort sort = Sort.by(Order.desc("id"));


        // 총 Tracks 수 본인이 아닐시에는 비공개 트랙 제외

        // 좋아요한 트랙 최대 3개
        // 비공개는 제외
        // 좋아요 누른순으로 가져와야 되고
        // 좋아요 총 갯수에 비공개 제외 해야하고
        List<LikedRedisDto> likeTrackIds = likesCommonService.getLikeTrackIds(profileUser, ContentsType.TRACK);
        // 좋아요한 숫자
        int totalLikedTrackCount = likeTrackIds.size();

        // 트랙 정보 불러오기
        // like
        // 플레이 횟수
        // report 수
        // reply 수
        if (totalLikedTrackCount > 0) {
            // 가장 최근 상위 3개

            
            int recentSize = Math.min(likeTrackIds.size(), 3);


        }
        // 팔로윙 한 유저 최대 3명


        // 가장 최근 댓글



        //

    }


}

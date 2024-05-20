package sky.Sss.domain.user.service.profile;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.track.SsbTrackLikes;
import sky.Sss.domain.track.service.track.TrackLikesService;
import sky.Sss.domain.track.service.track.TrackQueryService;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Enabled;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.domain.user.service.follows.UserFollowsService;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileService {

    private UserQueryService userQueryService;
    private TrackQueryService trackQueryService;
    private UserFollowsService userFollowsService;
    private TrackLikesService trackLikesService;


    public void getUserProfileByUserName(String userName) {

        // 본인 정보
        User user = userQueryService.findOne();

        User findUserName = userQueryService.findByUserName(userName, Enabled.ENABLED);

        // 본인 프로필 여부
        boolean isMyProfile = user.getToken().equals(findUserName.getToken());
        // 회원 닉네임
        // 회원 프로필 사진
        // 총 팔로워 수
        int followerTotalCount = userFollowsService.getFollowerTotalCount(findUserName);
        // 총 팔로잉 수
        int followingTotalCount = userFollowsService.getFollowingTotalCount(findUserName);

        // 총 Tracks 수 본인이 아닐시에는 비공개 트랙 제외
        // 좋아요한 트랙 총 3개
        List<SsbTrackLikes> userLikedTrackList = trackLikesService.getUserLikedTrackList(findUserName);
        // 좋아요한 숫자
        int totalLikedTrackCount = userLikedTrackList.size();

        if (totalLikedTrackCount > 0) {
            Set<Long> likedTrackId = userLikedTrackList.stream().map(liked -> liked.getSsbTrack().getId()).collect(Collectors.toSet());

        }

        // 팔로윙 한 사람 3명 총 수

        // 가장 최근 댓글
        //


    }


}

package sky.Sss.domain.user.controller;


import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.track.dto.common.like.TrackLikedWithCountDto;
import sky.Sss.domain.user.annotation.UserAuthorize;
import sky.Sss.domain.user.dto.rep.UserProfileDto;
import sky.Sss.domain.user.dto.req.UidListSearchReqDto;
import sky.Sss.domain.user.service.profile.UserProfileService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/profile")
@UserAuthorize
public class UserProfileController {


    private final UserProfileService userProfileService;
    /**
     *
     *
     * 캐시 미스 발생 시 처리 전략
     * 데이터베이스에서 직접 쿼리: 캐시에 데이터가 없거나 레디스 서비스 자체에 장애가 발생한 경우, 데이터베이스에서 직접 데이터를 쿼리하는 것이 기본적인 대응 방법입니다. 이는 데이터베이스의 부하를 증가시킬 수 있지만, 데이터의 정확성과 서비스의 가용성을 유지할 수 있습니다.
     * 캐시와 데이터베이스의 일관성 유지: 캐시에 데이터를 저장할 때는 데이터베이스와의 일관성을 유지하기 위한 전략이 필요합니다. 예를 들어, 댓글이 추가되거나 좋아요 수가 변경될 때 캐시와 데이터베이스를 동시에 업데이트하여 일관성을 유지합니다.
     * 캐시 복구 전략: 레디스 같은 캐시 시스템이 다운된 경우를 대비하여, 자동으로 재시작하거나 다른 노드로의 장애 전환(failover)이 가능한 설정을 구축하는 것이 좋습니다. 또한, 정기적으로 캐시를 데이터베이스의 데이터로부터 복구할 수 있는 로직을 구현하는 것이 좋습니다.
     * JOIN을 이용한 쿼리 전략
     * 데이터베이스에서 직접 JOIN을 사용하여 쿼리하는 방법은 다음과 같은 경우에 유용합니다:
     *
     * 캐시 미스가 빈번하지 않은 경우: 캐시 미스가 자주 발생하지 않는다면, 데이터베이스 부하가 그리 크지 않을 수 있으며, JOIN 쿼리를 통해 필요한 데이터를 안정적으로 얻을 수 있습니다.
     * 데이터 정확성이 매우 중요한 경우: 실시간으로 업데이트되어야 하는 중요한 데이터의 경우, 캐시보다는 항상 최신의 데이터를 제공할 수 있는 데이터베이스 쿼리가 더 적합할 수 있습니다.
     * 복잡한 쿼리가 필요한 경우: 여러 테이블 간의 관계를 다루어야 하고, 복잡한 데이터 처리가 필요한 경우, SQL의 JOIN을 이용하여 쿼리하는 것이 더 효과적일 수 있습니다.
     * 결론
     * 레디스와 같은 캐시 시스템을 사용할 때는 항상 캐시 미스나 캐시 서버 다운의 가능성을 고려해야 합니다. 이러한 상황에 대비하여 데이터베이스 쿼리를 통해 필요한 데이터를 안정적으로 얻을 수 있는 백업 전략을 마련하는 것이 중요합니다. 캐시와 데이터베이스 사이의 일관성을 유지하고, 장애 발생 시 빠르게 복구할 수 있는 전략을 구축하는 것이 서비스의 안정성을 보장하는 방법입니다.
     * @param username
     * @return
     */

    /**
     * 유저 프로필 페이지에 header 정보 반환
     * @param username
     * @return
     */
    @GetMapping("/{username}")
    public ResponseEntity<UserProfileDto> getUserProfileHeader (@PathVariable String username) {
        if (Objects.equals(username, "")) {
            throw new IllegalArgumentException();
        }
        return ResponseEntity.ok(userProfileService.getProfileHeaderByUserName(username));
    }


    @GetMapping("/list")
    public ResponseEntity<List<UserProfileDto>> getUserProfileInfoList (@ModelAttribute UidListSearchReqDto uidListSearchReqDto) {
        if (uidListSearchReqDto == null || uidListSearchReqDto.getIds().isEmpty()) {
            throw new IllegalArgumentException();
        }
        return ResponseEntity.ok(userProfileService.getUserInfoListByIds(
            new HashSet<>(uidListSearchReqDto.getIds())));
    }

//    @GetMapping("/list")
//    public ResponseEntity<List<UserProfileDto>> getUserProfileInfoList(
//        @RequestParam <Long> ids) {
//
//        if (ids == null || ids.isEmpty()) {
//            throw new IllegalArgumentException("Ids cannot be null or empty");
//        }
//
//        return ResponseEntity.ok(userProfileService.getUserInfoListByIds(ids));
//    }

    /**
     *
     * 사용자가 최근에 좋아요한 트랙 3개 및
     * 좋아요 총합
     *
     * @param uid
     * @return
     */
    @GetMapping("/like/recent/tracks/{uid}")
    public ResponseEntity<TrackLikedWithCountDto> getUserRecentLikeTracks (@PathVariable Long uid) {
        if (uid == 0) {
            throw new IllegalArgumentException();
        }
        return ResponseEntity.ok(userProfileService.getRecentLikedTracksWithCount(uid));
    }

    /**
     * 유저가 가장 최근 팔로우한 유저아이디 Top3 및 followTotal 을 반환
     * UserFollowing Recent Top3 List API
     */
    @GetMapping("/followings/recent/{uid}")
    public ResponseEntity<?> getUserRecentFollowingsList (@PathVariable Long uid) {
        if (uid == 0) {
            throw new IllegalArgumentException();
        }
        return ResponseEntity.ok(userProfileService.getRecentTop3FollowingUser(uid));
    }


    /**
     * 유저를 팔로우 하고 있는 유저 리스트 전부 반환
     */
    @GetMapping("/followers/{uid}")
    public ResponseEntity<?> getUserFollowerList (@PathVariable Long uid) {
        if (uid == 0) {
            throw new IllegalArgumentException();
        }
        return ResponseEntity.ok(userProfileService.getFollowerUserList(uid));
    }
    /**
     * 유저가 팔로우 하고 있는 유저 리스트 전부 반환
     */
    @GetMapping("/following/{uid}")
    public ResponseEntity<?> getUserFollowingList (@PathVariable Long uid) {
        if (uid == 0) {
            throw new IllegalArgumentException();
        }
        return ResponseEntity.ok(userProfileService.getFollowingUserList(uid));
    }
}

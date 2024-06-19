package sky.Sss.domain.user.service.profile;


import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.common.like.LikedRedisDto;
import sky.Sss.domain.track.dto.common.like.TrackTargetWithCountDto;
import sky.Sss.domain.track.dto.track.rep.TrackDetailDto;
import sky.Sss.domain.track.dto.track.rep.TrackUploadCountDto;
import sky.Sss.domain.track.service.common.LikesCommonService;
import sky.Sss.domain.track.service.track.TrackInfoService;
import sky.Sss.domain.track.service.track.TrackQueryService;
import sky.Sss.domain.user.dto.myInfo.UserMyInfoDto;
import sky.Sss.domain.user.dto.myInfo.UserProfileRepDto;
import sky.Sss.domain.user.dto.redis.RedisFollowsDto;
import sky.Sss.domain.user.dto.rep.UserProfileHeaderDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.UserFollows;
import sky.Sss.domain.user.model.ContentsType;
import sky.Sss.domain.user.model.Enabled;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.domain.user.service.follows.UserFollowsService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileService {

    private final UserQueryService userQueryService;
    private final UserFollowsService userFollowsService;
    private final LikesCommonService likesCommonService;
    private final TrackInfoService trackInfoService;
    private final TrackQueryService trackQueryService;

    public UserMyInfoDto getUserMyInfoDto() {
        User user = userQueryService.findOne();

        List<LikedRedisDto> likeTrackIds = likesCommonService.getLikeTrackIds(user, ContentsType.TRACK);

        // 좋아하는 트랙리스트
        List<Long> userLikedList = likeTrackIds.stream().map(LikedRedisDto::getTargetId).toList();
        List<RedisFollowsDto> followingUsersFromCacheOrDB = userFollowsService.getFollowingUsersFromCacheOrDB(user);
        List<RedisFollowsDto> followerUsersFromCacheOrDB = userFollowsService.getFollowersUsersFromCacheOrDB(user);

        List<Long> followingIds = followingUsersFromCacheOrDB.stream().map(RedisFollowsDto::getFollowingUid)
            .toList();
        List<Long> followerIds = followerUsersFromCacheOrDB.stream().map(RedisFollowsDto::getFollowerUid)
            .toList();


        return new UserMyInfoDto(user.getUserId(), user.getEmail(), user.getUserName(), user.getPictureUrl(),
            user.getIsLoginBlocked(), user.getGrade(), userLikedList, followingIds,followerIds);
    }


    public UserProfileHeaderDto getProfileHeaderByUserName(String userName) {
        User profileUser = userQueryService.findByUserName(userName, Enabled.ENABLED);
        return getProfileHeader(profileUser);
    }

    /**
     * 유저 팔로윙,팔로우,업로드 트랙 수를 가져오는 API
     */
    public UserProfileHeaderDto getProfileHeader(User profileUser) {
        // 본인 정보
        User user = userQueryService.findOne();
        // 본인 프로필 여부
        boolean isMyProfile = user.getToken().equals(profileUser.getToken());

        // 내프로필이 아닐경우 팔로우 여부 확인
        TrackUploadCountDto uploadCountDto = null;

        // 총 Tracks 수 본인이 아닐시에는 비공개 트랙 제외
        if (isMyProfile) {
            uploadCountDto = trackQueryService.getMyUploadCount(profileUser, Status.ON);
        } else {
            uploadCountDto = trackQueryService.getUserUploadCount(profileUser, Status.ON);
        }

        // 팔로잉 리스트
        List<RedisFollowsDto> userFollowingList = userFollowsService.getFollowingUsersFromCacheOrDB(profileUser);
        int totalFollowingCount = userFollowingList.size();

        // 팔로워 유저 전부
        // 회원 닉네임
        // 회원 프로필 사진
        // 팔로워 리스트
        List<RedisFollowsDto> userFollowsList = userFollowsService.getFollowersUsersFromCacheOrDB(profileUser);
        int totalFollowerCount = userFollowsList.size();
        return UserProfileHeaderDto.builder().uid(profileUser.getId())
            .userName(profileUser.getUserName())
            .followerCount(totalFollowerCount)
            .followingCount(totalFollowingCount)
            .trackTotalCount(Math.toIntExact(uploadCountDto.getTotalCount()))
            .pictureUrl(profileUser.getPictureUrl()).build();
    }


    /**
     * 가장 최근 좋아요한 트랙 3개 및 like 수
     *
     * @param user
     * @param profileUser
     * @return
     */
    public TrackTargetWithCountDto getRecentLikedTracksWithCount(User user, User profileUser) {
        // 좋아요한 트랙 최대 3개
        // 비공개는 제외
        // 좋아요 누른순으로 가져와야 되고
        // 좋아요 총 갯수에 비공개 제외 해야하고
        List<LikedRedisDto> likedRedisDtoList = likesCommonService.getLikeTrackIds(profileUser, ContentsType.TRACK);
        // 좋아요한 숫자
        int totalLikedTrackCount = likedRedisDtoList.size();

        TrackTargetWithCountDto trackTargetWithCountDto = new TrackTargetWithCountDto(totalLikedTrackCount);

        // 트랙 정보 불러오기
        // like
        // 플레이 횟수
        // report 수
        // reply 수
        if (totalLikedTrackCount > 0) {
            int recentSize = Math.min(likedRedisDtoList.size(), 3);

            // 가장 최근 상위 3개
            // id 내림차순으로 정렬
            likedRedisDtoList.sort(Comparator.comparing(LikedRedisDto::getId).reversed());

            List<LikedRedisDto> recentLikeTracks = likedRedisDtoList.subList(0, recentSize);

            Map<Long, LikedRedisDto> likedToMap = recentLikeTracks.stream()
                .collect(Collectors.toMap(LikedRedisDto::getTargetId, value -> value));

            List<TrackDetailDto> trackDetailDtoList = trackInfoService.getTrackInfoList(likedToMap.keySet(), user);

            for (TrackDetailDto trackDetailDto : trackDetailDtoList) {
                LikedRedisDto likedRedisDto = likedToMap.get(trackDetailDto.getTrackInfo().getId());
                if (likedRedisDto != null) {
                    trackTargetWithCountDto.addTarget(likedRedisDto.getId(), trackDetailDto,
                        likedRedisDto.getCreatedDateTime());
                }
            }
        }
        return trackTargetWithCountDto;
    }


    /**
     * 해당 유저의 간단정보
     * 업로드 트랙 수 및 팔로워 수
     *
     * @return
     */
    public List<UserProfileHeaderDto> getUserSearchInfoList(Set<Long> ids) {

        List<User> usersByIds = userQueryService.findUsersByIds(ids, Enabled.ENABLED);

        return null;
    }


}

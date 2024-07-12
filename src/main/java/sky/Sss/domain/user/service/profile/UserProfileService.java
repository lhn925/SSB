package sky.Sss.domain.user.service.profile;


import java.util.ArrayList;
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
import sky.Sss.domain.track.dto.common.like.TrackLikedWithCountDto;
import sky.Sss.domain.track.dto.common.like.TrackLikedWithCountDto.TrackInfo;
import sky.Sss.domain.track.dto.track.rep.TrackDetailDto;
import sky.Sss.domain.track.dto.track.rep.TrackUploadCountDto;
import sky.Sss.domain.track.service.common.LikesCommonService;
import sky.Sss.domain.track.service.track.TrackInfoService;
import sky.Sss.domain.track.service.track.TrackQueryService;
import sky.Sss.domain.user.dto.follows.FollowsUserListDto;
import sky.Sss.domain.user.dto.myInfo.UserMyInfoDto;
import sky.Sss.domain.user.dto.redis.RedisFollowsDto;
import sky.Sss.domain.user.dto.rep.UserProfileDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.ContentsType;
import sky.Sss.domain.user.model.Enabled;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.domain.user.service.follows.UserFollowsService;
import sky.Sss.global.redis.dto.RedisKeyDto;

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
        List<LikedRedisDto> likeTrackIds = likesCommonService.getVisibleLikeTracksForUser(user, user,
            ContentsType.TRACK);
        // 좋아하는 트랙리스트
        List<Long> userTrackLikedList = likeTrackIds.stream().map(LikedRedisDto::getTargetId).toList();
        // 유저가 팔로우 하고 있는 유저 idList
        List<RedisFollowsDto> followingUsersFromCacheOrDB = userFollowsService.getFollowingUsersFromCacheOrDB(user);
        // 유저를 팔로우 하고 있는 유저 idList
        List<RedisFollowsDto> followerUsersFromCacheOrDB = userFollowsService.getFollowersUsersFromCacheOrDB(user);

        TrackUploadCountDto myUploadCount = trackQueryService.getMyUploadCount(user, Status.ON);

        List<Long> followingIds = followingUsersFromCacheOrDB.stream().map(RedisFollowsDto::getFollowingUid)
            .toList();
        List<Long> followerIds = followerUsersFromCacheOrDB.stream().map(RedisFollowsDto::getFollowerUid)
            .toList();

        return new UserMyInfoDto(user.getId(), user.getUserId(), user.getEmail(), user.getUserName(),
            user.getPictureUrl(),
            user.getIsLoginBlocked(), user.getGrade(), userTrackLikedList, followingIds, followerIds,
            Math.toIntExact(myUploadCount.getTotalCount()));
    }


    public UserProfileDto getProfileHeaderByUserName(String userName) {
        User profileUser = userQueryService.findByUserName(userName, Enabled.ENABLED);
        return getProfileHeader(profileUser);
    }

    /**
     * 유저 팔로잉 , 팔로우 , 업로드 트랙 수를 가져오는 API
     */
    public UserProfileDto getProfileHeader(User profileUser) {
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
        return UserProfileDto.builder().id(profileUser.getId())
            .userName(profileUser.getUserName())
            .followerCount(totalFollowerCount)
            .followingCount(totalFollowingCount)
            .trackTotalCount(Math.toIntExact(uploadCountDto.getTotalCount()))
            .pictureUrl(profileUser.getPictureUrl()).build();
    }


    /**
     * 유저의 아이디 리스트 다중 검색 후 해당 유저아이디의 해당하는 팔로윙,팔로우,업로드 트랙 수를 가져오는 API
     */
    public List<UserProfileDto> getUserInfoListByIds(Set<Long> uIds) {
        List<User> users = userQueryService.findUsersByIds(uIds, Enabled.ENABLED);

        List<String> tokens = users.stream().map(User::getToken).toList();

        // 트랙 수
        Map<String, TrackUploadCountDto> usersUploadCount = trackQueryService.getUsersUploadCount(users, Status.ON);

        // 팔로워 수
        Map<String, List<RedisFollowsDto>> followerMap = userFollowsService.getFollowMapFromCacheOrDBByType(
            tokens, RedisKeyDto.REDIS_USER_FOLLOWER_MAP_KEY);

        // 팔로잉 수
        Map<String, List<RedisFollowsDto>> followingMap = userFollowsService.getFollowMapFromCacheOrDBByType(
            tokens, RedisKeyDto.REDIS_USER_FOLLOWING_MAP_KEY);

        List<UserProfileDto> userProfileDtoList = new ArrayList<>();

        for (User user : users) {
            TrackUploadCountDto uploadCountDto = usersUploadCount.get(String.valueOf(user.getId()));

            int trackTotalCount = Math.toIntExact(uploadCountDto.getTotalCount());

            List<RedisFollowsDto> followerList = followerMap.get(user.getToken());

            int followerCount = followerList != null ? followerList.size() : 0;

            List<RedisFollowsDto> followingList = followingMap.get(user.getToken());

            int followingCount = followingList != null ? followingList.size() : 0;
            UserProfileDto userProfileDto = UserProfileDto.builder()
                .userName(user.getUserName()).id(user.getId())
                .pictureUrl(user.getPictureUrl())
                .trackTotalCount(trackTotalCount)
                .followerCount(followerCount)
                .followingCount(followingCount).build();
            userProfileDtoList.add(userProfileDto);
        }
        return userProfileDtoList;

    }

    /**
     * 가장 최근 좋아요한 트랙 3개 및 like 수
     *
     * @return
     */
    public TrackLikedWithCountDto getRecentLikedTracksWithCount(Long uid) {
        // 좋아요한 트랙 최대 3개
        // 비공개는 제외
        // 좋아요 누른순으로 가져와야 되고
        // 좋아요 총 갯수에 비공개 제외 해야하고

        User user = userQueryService.findOne();

        User profileUser = userQueryService.findOne(uid, Enabled.ENABLED);
        /**
         * 프로필 유저의 좋아요 트랙 곡 중 자신의 비공개곡이 있는 경우엔 허용
         * 프로필 유저의 자신의 트랙곡은 허용
         *
         */
        List<LikedRedisDto> likedRedisDtoList = likesCommonService.getVisibleLikeTracksForUser(profileUser, profileUser,
            ContentsType.TRACK);
        // 좋아요한 숫자
        int totalLikedTrackCount = likedRedisDtoList.size();

        TrackLikedWithCountDto trackTargetWithCountDto = new TrackLikedWithCountDto(totalLikedTrackCount);

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

            trackTargetWithCountDto.getTargetInfos().sort(Comparator.comparing(TrackInfo::getId).reversed());
        }
        return trackTargetWithCountDto;
    }

    /**
     * 유저가 가장 최근 팔로우한 유저아이디 Top3 및 followTotal 을 반환
     * UserFollowing Recent Top3 List API
     */
    public FollowsUserListDto getRecentTop3FollowingUser(Long uid) {
        User profileUser = userQueryService.findOne(uid, Enabled.ENABLED);

        List<RedisFollowsDto> followingList = new ArrayList<>(
            userFollowsService.getFollowingUsersFromCacheOrDB(profileUser));

        List<RedisFollowsDto> recentFollows = new ArrayList<>();

        if (followingList.size() > 0) {

            int recentSize = Math.min(followingList.size(), 3);

            // 가장 상위 3개 내림차순으로 정렬
            followingList.sort(Comparator.comparing(RedisFollowsDto::getId).reversed());

            recentFollows.addAll(followingList.subList(0, recentSize));
        }
        return new FollowsUserListDto(recentFollows, followingList.size());
    }


    /**
     * 유저를 팔로우 하고 있는 유저 리스트 전부 출력
     */
    public FollowsUserListDto getFollowerUserList(Long uid) {
        User profileUser = userQueryService.findOne(uid, Enabled.ENABLED);

        List<RedisFollowsDto> followerList = new ArrayList<>(
            userFollowsService.getFollowersUsersFromCacheOrDB(profileUser));

        return new FollowsUserListDto(followerList, followerList.size());
    }

    /**
     * 유저가 팔로우 하고 있는 유저 리스트 전부 출력
     */
    public FollowsUserListDto getFollowingUserList(Long uid) {
        User profileUser = userQueryService.findOne(uid, Enabled.ENABLED);

        List<RedisFollowsDto> followingList = new ArrayList<>(
            userFollowsService.getFollowingUsersFromCacheOrDB(profileUser));

        return new FollowsUserListDto(followingList, followingList.size());
    }


}

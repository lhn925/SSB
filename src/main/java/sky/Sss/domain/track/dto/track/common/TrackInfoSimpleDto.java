package sky.Sss.domain.track.dto.track;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import sky.Sss.domain.track.entity.track.SsbTrackLikes;
import sky.Sss.domain.user.dto.UserSimpleInfoDto;
import sky.Sss.domain.user.dto.myInfo.UserProfileRepDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.UserFollows;

@Slf4j
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class TrackInfoSimpleDto {

    // 트랙 아이디
    private Long id;
    private String token;
    private String title;

    private Integer trackLength;
    private String coverUrl;
    private Boolean isPrivacy;

    // 하트 여부
    private Boolean isLike;

    private Boolean isOwner;

    private LocalDateTime createdDateTime;

    private UserProfileRepDto postUser;

    public TrackInfoSimpleDto(
        Long id,
        String token,
        String title,
        User ownerUser,
        Integer trackLength,
        String coverUrl,
        Boolean isPrivacy,
        LocalDateTime createdDateTime) {
        this.id = id;
        this.title = title;
        this.token = token;
        this.postUser = new UserProfileRepDto(ownerUser);
        this.trackLength = trackLength;
        this.coverUrl = coverUrl;
        this.isPrivacy = isPrivacy;
        this.createdDateTime = createdDateTime;
        this.isOwner = false;
        this.isLike = false;
    }

    public TrackInfoSimpleDto(
        Long id,
        String title,
        User ownerUser,
        Integer trackLength,
        String coverUrl,
        Boolean isPrivacy,
        LocalDateTime createdDateTime) {
        this.id = id;
        this.title = title;
        this.postUser = new UserProfileRepDto(ownerUser);
        this.trackLength = trackLength;
        this.coverUrl = getCoverUrl(coverUrl, ownerUser);
        this.isPrivacy = isPrivacy;
        this.createdDateTime = createdDateTime;
        this.isOwner = false;
        this.isLike = false;
    }

    public TrackInfoSimpleDto(Long id,
        String token,
        String title,
        Integer trackLength,
        String coverUrl,
        Boolean isPrivacy,
        User ownerUser,
        Long likedUserId,
        SsbTrackLikes ssbTrackLikes,
        UserFollows userFollows,
        LocalDateTime createdDateTime) {
        boolean isOwner = Objects.equals(ownerUser.getId(), likedUserId);
        this.id = id;
        if (isOwner) {
            this.token = token;
        }
        this.title = title;
        this.postUser = new UserProfileRepDto(ownerUser);
        this.trackLength = trackLength;
        this.coverUrl = getCoverUrl(coverUrl, ownerUser);
        this.isPrivacy = isPrivacy;
        this.isOwner = isOwner;
        this.isLike = ssbTrackLikes != null;
        UserProfileRepDto.updateIsFollow(this.postUser, userFollows != null);
        this.createdDateTime = createdDateTime;
    }


    public TrackInfoSimpleDto(Long id, String token, String title, Integer trackLength, String coverUrl,
        Boolean isPrivacy,
        Boolean isLike, Boolean isOwner, LocalDateTime createdDateTime, UserProfileRepDto userProfileRepDto) {
        this.id = id;
        this.token = token;
        this.title = title;
        this.trackLength = trackLength;
        this.coverUrl = coverUrl;
        this.isPrivacy = isPrivacy;
        this.isLike = isLike;
        this.isOwner = isOwner;
        this.createdDateTime = createdDateTime;
        this.postUser = userProfileRepDto;
    }

    // 없으면 해당 사용자의 프로필 사진
    private static String getCoverUrl(String coverUrl, User ownerUser) {
        return coverUrl == null ? ownerUser.getPictureUrl() : coverUrl;
    }

    public static void updateIsLike(TrackInfoSimpleDto trackInfoSimpleDto, boolean isLike) {
        trackInfoSimpleDto.setIsLike(isLike);
    }

    public static void updateIsOwner(TrackInfoSimpleDto trackInfoSimpleDto, boolean isOwner) {
        trackInfoSimpleDto.setIsOwner(isOwner);
    }

    public static void updateToken(TrackInfoSimpleDto trackInfoSimpleDto, String token) {
        trackInfoSimpleDto.setToken(token);
    }

    public static void updateCoverUrl(TrackInfoSimpleDto trackInfoSimpleDto, String coverUrl) {
        trackInfoSimpleDto.setCoverUrl(coverUrl);
    }


}

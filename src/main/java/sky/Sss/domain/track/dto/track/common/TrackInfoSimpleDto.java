package sky.Sss.domain.track.dto.track.common;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.SsbTrackLikes;
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

    private LocalDateTime createdDateTime;

    private Boolean isOwner;
    private UserProfileRepDto postUser;

//    public TrackInfoSimpleDto(
//        Long id,
//        String token,
//        String title,
//        User ownerUser,
//        Integer trackLength,
//        String coverUrl,
//        Boolean isPrivacy,
//        LocalDateTime createdDateTime) {
//        this.id = id;
//        this.title = title;
//        this.token = token;
//        this.postUser = new UserProfileRepDto(ownerUser);
//        this.trackLength = trackLength;
//        this.coverUrl = coverUrl;
//        this.isPrivacy = isPrivacy;
//        this.createdDateTime = createdDateTime;
//        this.isOwner = false;
//    }
//
//    public TrackInfoSimpleDto(
//        Long id,
//        String title,
//        User ownerUser,
//        Integer trackLength,
//        String coverUrl,
//        Boolean isPrivacy,
//        LocalDateTime createdDateTime) {
//        this.id = id;
//        this.title = title;
//        this.postUser = new UserProfileRepDto(ownerUser);
//        this.trackLength = trackLength;
//        this.coverUrl = getCoverUrl(coverUrl, ownerUser);
//        this.isPrivacy = isPrivacy;
//        this.createdDateTime = createdDateTime;
//        this.isOwner = false;
//    }
//
//
//
//
//    public TrackInfoSimpleDto(Long id,
//        String token,
//        String title,
//        Integer trackLength,
//        String coverUrl,
//        Boolean isPrivacy,
//        User ownerUser,
//        Long likedUserId,
//        LocalDateTime createdDateTime) {
//        boolean isOwner = Objects.equals(ownerUser.getId(), likedUserId);
//        this.id = id;
//        if (isOwner) {
//            this.token = token;
//        }
//        this.title = title;
//        this.postUser = new UserProfileRepDto(ownerUser);
//        this.trackLength = trackLength;
//        this.coverUrl = getCoverUrl(coverUrl, ownerUser);
//        this.isPrivacy = isPrivacy;
//        this.isOwner = isOwner;
//        this.createdDateTime = createdDateTime;
//    }

    @Builder
    public TrackInfoSimpleDto(Long id, String title, Integer trackLength, String coverUrl,
        Boolean isPrivacy, LocalDateTime createdDateTime, UserProfileRepDto postUser) {
        this.id = id;
        this.title = title;
        this.trackLength = trackLength;
        this.coverUrl = coverUrl;
        this.isPrivacy = isPrivacy;
        this.createdDateTime = createdDateTime;
        this.postUser = postUser;
    }





    // 없으면 해당 사용자의 프로필 사진
    public static String getCoverUrl(String coverUrl, User ownerUser) {
        return coverUrl == null ? ownerUser.getPictureUrl() : coverUrl;
    }


    public static TrackInfoSimpleDto create(SsbTrack ssbTrack) {
        return TrackInfoSimpleDto.builder()
            .id(ssbTrack.getId())
            .title(ssbTrack.getTitle())
            .trackLength(ssbTrack.getTrackLength())
            .coverUrl(getCoverUrl(ssbTrack.getCoverUrl(), ssbTrack.getUser()))
            .isPrivacy(ssbTrack.getIsPrivacy())
            .createdDateTime(ssbTrack.getCreatedDateTime())
            .postUser(new UserProfileRepDto(ssbTrack.getUser())).build();
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

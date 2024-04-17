package sky.Sss.domain.track.dto.track;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class TrackInfoSimpleDto {

    private Long id;
    private String token;
    private String title;
    private String userName;
    private Integer trackLength;
    private String coverUrl;
    private Boolean isPrivacy;
    // 하트 여부
    private Boolean isLike;

    private Boolean isOwner;

    private LocalDateTime createdDateTime;

    public TrackInfoSimpleDto(Long id, String token, String title, String userName, Integer trackLength,
        String coverUrl,
        Boolean isPrivacy, LocalDateTime createdDateTime) {
        this.id = id;
        this.title = title;
        this.token = token;
        this.userName = userName;
        this.trackLength = trackLength;
        this.coverUrl = coverUrl;
        this.isPrivacy = isPrivacy;
        this.createdDateTime = createdDateTime;
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

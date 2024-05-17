package sky.Sss.domain.track.dto.track.redis;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.model.MainGenreType;
import sky.Sss.domain.user.entity.User;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class RedisTrackDto {

    private Long id;
    private String title;
    private Long uid;
    private MainGenreType mainGenreType;
    private String genre;
    private Boolean isDownload;
    private String description;
    private String coverUrl;
    private Boolean isPrivacy;
    private Long size;
    private Integer trackLength;
    private String token;
    private String originalName;
    private String storeFileName;
    private Boolean isRelease;
    private Boolean isStatus;
    private LocalDateTime createdDateTime;
    private LocalDateTime lastModifiedDateTime;


    @Builder
    private RedisTrackDto(Long id, String title, Long uid, MainGenreType mainGenreType, String genre, Boolean isDownload,
        String description, String coverUrl, Boolean isPrivacy, Long size, Integer trackLength, String token,
        String originalName, String storeFileName, Boolean isRelease, Boolean isStatus, LocalDateTime createdDateTime,
        LocalDateTime lastModifiedDateTime) {
        this.id = id;
        this.title = title;
        this.uid = uid;
        this.mainGenreType = mainGenreType;
        this.genre = genre;
        this.isDownload = isDownload;
        this.description = description;
        this.coverUrl = coverUrl;
        this.isPrivacy = isPrivacy;
        this.size = size;
        this.trackLength = trackLength;
        this.token = token;
        this.originalName = originalName;
        this.storeFileName = storeFileName;
        this.isRelease = isRelease;
        this.isStatus = isStatus;
        this.createdDateTime = createdDateTime;
        this.lastModifiedDateTime = lastModifiedDateTime;
    }


    public static RedisTrackDto create(SsbTrack ssbTrack) {
        return RedisTrackDto.builder().id(ssbTrack.getId()).title(ssbTrack.getTitle())
            .uid(ssbTrack.getUser().getId()).mainGenreType(ssbTrack.getMainGenreType())
            .genre(ssbTrack.getGenre()).isDownload(ssbTrack.getIsDownload()).description(ssbTrack.getDescription())
            .coverUrl(ssbTrack.getCoverUrl()).isPrivacy(ssbTrack.getIsPrivacy()).size(ssbTrack.getSize())
            .trackLength(ssbTrack.getTrackLength()).token(ssbTrack.getToken()).originalName(ssbTrack.getOriginalName())
            .storeFileName(ssbTrack.getStoreFileName()).isRelease(ssbTrack.getIsRelease())
            .isStatus(ssbTrack.getIsStatus())
            .createdDateTime(ssbTrack.getCreatedDateTime()).lastModifiedDateTime(ssbTrack.getLastModifiedDateTime())
            .build();
    }


}

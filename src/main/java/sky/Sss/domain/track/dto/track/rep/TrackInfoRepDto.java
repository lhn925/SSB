package sky.Sss.domain.track.dto.track.rep;


import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.entity.track.SsbTrack;

@Setter
@Getter
@NoArgsConstructor
public class TrackInfoRepDto {

    private Long id;
    private String token;
    private String title;
    private String coverUrl;
    private String userName;
    private Integer trackLength;
    private LocalDateTime createdDateTime;

    public TrackInfoRepDto(Long id, String token, String title, String coverUrl, String userName, Integer trackLength,
        LocalDateTime createdDateTime) {
        this.id = id;
        this.token = token;
        this.title = title;
        this.coverUrl = coverUrl;
        this.userName = userName;
        this.trackLength = trackLength;
        this.createdDateTime = createdDateTime;
    }

    public static TrackInfoRepDto create(SsbTrack ssbTrack) {
        TrackInfoRepDto trackInfoRepDto = new TrackInfoRepDto();
        trackInfoRepDto.setId(ssbTrack.getId());
        trackInfoRepDto.setToken(ssbTrack.getToken());
        trackInfoRepDto.setTitle(ssbTrack.getTitle());
        trackInfoRepDto.setCoverUrl(ssbTrack.getCoverUrl());
        trackInfoRepDto.setUserName(ssbTrack.getUser().getUserName());
        trackInfoRepDto.setTrackLength(ssbTrack.getTrackLength());
        trackInfoRepDto.setCreatedDateTime(ssbTrack.getCreatedDateTime());
        return trackInfoRepDto;
    }
}

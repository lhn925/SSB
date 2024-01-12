package sky.Sss.domain.track.dto.track;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.entity.track.SsbTrack;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrackInfoRepDto {

    private Long id;
    private String token;
    private String title;
    private String coverUrl;
    private String userName;
    private Integer trackLength;


    public static TrackInfoRepDto create(SsbTrack ssbTrack) {
        TrackInfoRepDto trackInfoRepDto = new TrackInfoRepDto();
        trackInfoRepDto.setId(ssbTrack.getId());
        trackInfoRepDto.setToken(ssbTrack.getToken());
        trackInfoRepDto.setTitle(ssbTrack.getTitle());
        trackInfoRepDto.setCoverUrl(ssbTrack.getCoverUrl());
        trackInfoRepDto.setUserName(ssbTrack.getUser().getUserName());
        trackInfoRepDto.setTrackLength(ssbTrack.getTrackLength());
        return trackInfoRepDto;
    }
}

package sky.Sss.domain.track.dto.playlist.redis;


import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.entity.playList.SsbPlayListTracks;

@Getter
@Setter(value = AccessLevel.PRIVATE)
@NoArgsConstructor
public class PlyTracksPositionRedisDto {

    private Long id;
    private Long settings_id;
    private Long track_id;
    private Integer position;
    private LocalDateTime createdDateTime;

    public PlyTracksPositionRedisDto(Long id, Long settings_id, Long track_id,
        Integer position, LocalDateTime createdDateTime) {
        this.id = id;
        this.settings_id = settings_id;
        this.track_id = track_id;
        this.position = position;
        this.createdDateTime = createdDateTime;
    }

    public PlyTracksPositionRedisDto(SsbPlayListTracks ssbPlayListTracks) {
        this.id = ssbPlayListTracks.getId();
        this.track_id = ssbPlayListTracks.getSsbTrack().getId();
        this.settings_id = ssbPlayListTracks.getSsbPlayListSettings().getId();
        this.position = ssbPlayListTracks.getPosition();
        this.createdDateTime = ssbPlayListTracks.getCreatedDateTime();

    }
}

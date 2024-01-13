package sky.Sss.domain.track.dto.track;

import static lombok.AccessLevel.PRIVATE;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.dto.track.log.TrackPlayLogRepDto;
import sky.Sss.domain.track.entity.track.SsbTrack;

@Getter
@Setter(PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrackPlayRepDto implements Serializable {

    private Long id;
    private String token;
    private String title;
    private String userName;
    private Integer trackLength;
    private TrackPlayLogRepDto trackPlayLogRepDto;

    public static TrackPlayRepDto create(SsbTrack ssbTrack) {
        TrackPlayRepDto trackPlayDto = new TrackPlayRepDto();
        trackPlayDto.setId(ssbTrack.getId());
        trackPlayDto.setToken(ssbTrack.getToken());
        trackPlayDto.setTitle(ssbTrack.getTitle());
        trackPlayDto.setUserName(ssbTrack.getUser().getUserName());
        trackPlayDto.setTrackLength(ssbTrack.getTrackLength());
        // 서버 호출 시간

        return trackPlayDto;
    }

    public static void updateTrackPlayLogRepDto(TrackPlayRepDto trackPlayRepDto ,TrackPlayLogRepDto trackPlayLogRepDto ) {
        // 조회수 측정 정보
        trackPlayRepDto.setTrackPlayLogRepDto(trackPlayLogRepDto);
    }
}

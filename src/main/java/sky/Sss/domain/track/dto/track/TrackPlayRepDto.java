package sky.Sss.domain.track.dto.track;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.dto.track.count.TrackCountRepDto;
import sky.Sss.domain.track.entity.chart.SsbChartIncludedPlays;
import sky.Sss.domain.track.entity.track.SsbTrack;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrackPlayRepDto implements Serializable {

    private Long id;
    private String title;
    private String userName;
    private String token;
    private Integer trackLength;

    private TrackCountRepDto trackCountRepDto;

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

    public void setTrackCountRepDto(SsbChartIncludedPlays ssbChartIncludedPlays) {
        // 조회수 측정 정보
        if (ssbChartIncludedPlays != null) {
            TrackCountRepDto trackCountRepDto = TrackCountRepDto.create(ssbChartIncludedPlays);
            this.trackCountRepDto = trackCountRepDto;
        }
    }
}

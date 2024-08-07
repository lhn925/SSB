package sky.Sss.domain.track.dto.track.chart;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.entity.track.SsbTrack;


@Getter
@Setter
@AllArgsConstructor
public class TrackTotalPlaysDto {
    private SsbTrack ssbTrack;
    private Long totalCount;
    private Integer dayTime;
    private Integer prevRanking;

}

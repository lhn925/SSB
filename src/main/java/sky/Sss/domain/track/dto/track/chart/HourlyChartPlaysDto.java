package sky.Sss.domain.track.dto.track.chart;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.entity.track.SsbTrack;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HourlyChartPlaysDto {
    private SsbTrack ssbTrack;
    private Long hourCount;
    private int dayTime;
}

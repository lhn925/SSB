package sky.Sss.domain.track.dto.track.chart;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.entity.track.SsbTrack;


@Getter
@Setter
@NoArgsConstructor
public class ChartHourCreateDto {
    private SsbTrack ssbTrack;
    private int ranking;
    private int dayTime;
    private long hourCount;
    private long dayTotalCount;
    private double score;
    private int prevRanking;

}

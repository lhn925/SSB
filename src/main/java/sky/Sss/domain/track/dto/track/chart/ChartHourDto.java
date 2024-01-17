package sky.Sss.domain.track.dto.track.chart;

import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.entity.track.SsbTrack;


@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class ChartHourDto {
    private SsbTrack ssbTrack;
    private Long hourCount;
    private Integer hour;
    private LocalDate createdDate;

    public ChartHourDto(SsbTrack ssbTrack, Long count, Integer hour, LocalDate createdDate) {
        this.ssbTrack = ssbTrack;
        this.hourCount = count;
        this.hour = hour;
        this.createdDate = createdDate;
    }
}

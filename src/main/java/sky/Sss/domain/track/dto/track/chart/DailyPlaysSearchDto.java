package sky.Sss.domain.track.dto.track.chart;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter(AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class DailyPlaysSearchDto {
    private Long trackId;
    private Long totalCount;
}

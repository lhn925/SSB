package sky.Sss.domain.track.dto.track.log;


import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class TrackChartLogSaveListReqDto {
    @NotNull
    private List<TrackChartSaveReqDto> list;
}

package sky.Sss.domain.track.dto.track.log;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.dto.track.TrackInfoReqDto;
import sky.Sss.domain.track.model.PlayStatus;


@Getter
@Setter
@NoArgsConstructor
public class TrackPlayLogModifyReqDto {

    @NotNull
    private Long id; // count ID;
    @NotBlank
    private String token;// count Token

    private int playTime;

    private long closeTime;

    private boolean isChartLog;// 플레이 상태
    @NotNull
    private TrackInfoReqDto trackInfoReqDto;
}

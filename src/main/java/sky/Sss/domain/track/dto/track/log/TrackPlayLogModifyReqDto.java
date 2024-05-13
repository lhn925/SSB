package sky.Sss.domain.track.dto.track.log;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.dto.track.req.TrackInfoReqDto;


@Getter
@Setter
@NoArgsConstructor
public class TrackPlayLogModifyReqDto {
    @NotBlank
    private String token;// count Token

    private int playTime;

    private long closeTime;

    private boolean isChartLog;// 플레이 상태
    @NotNull
    private TrackInfoReqDto trackInfoReqDto;
}

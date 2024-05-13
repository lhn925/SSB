package sky.Sss.domain.track.dto.track.log;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.dto.track.rep.TrackInfoRepDto;


@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrackPlayLogModifyRepDto {

    @NotNull
    private Long id; // count ID;
    @NotBlank
    private String token;// count Token

    private boolean isChartLog;// 플레이 상태

    @NotNull
    private TrackInfoRepDto trackInfoReqDto;


}

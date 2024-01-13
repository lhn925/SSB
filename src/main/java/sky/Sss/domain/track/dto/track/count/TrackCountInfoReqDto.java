package sky.Sss.domain.track.dto.track.count;


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
public class TrackCountInfoReqDto {

    @NotNull
    private Long id; // count ID;
    @NotBlank
    private String token;// count Token
    @NotNull
    private Integer playTime;// 플레이 시간
    @NotNull
    private PlayStatus playStatus;// 플레이 상태
    @NotNull
    private Long closeTime; //끝난 시간
    @NotNull
    private TrackInfoReqDto trackInfoReqDto;
}

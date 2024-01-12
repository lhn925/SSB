package sky.Sss.domain.track.dto.track.count;


import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.dto.track.TrackInfoReqDto;
import sky.Sss.domain.track.model.PlayBackStatus;


@Getter
@NoArgsConstructor
public class TrackCountReqListDto {
    @NotNull
    private List<TrackCountInfoReqDto> list;
}

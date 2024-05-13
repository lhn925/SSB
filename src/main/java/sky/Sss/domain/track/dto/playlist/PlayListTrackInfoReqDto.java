package sky.Sss.domain.track.dto.playlist;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.domain.track.dto.track.req.TrackInfoSaveReqDto;

@Getter
@Setter
public class PlayListTrackInfoReqDto extends TrackInfoSaveReqDto {
    // 순서
    @NotNull
    private Integer order;
}

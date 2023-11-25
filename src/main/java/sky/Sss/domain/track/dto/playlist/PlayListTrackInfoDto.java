package sky.Sss.domain.track.dto.playlist;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.domain.track.dto.track.TrackInfoSaveDto;

@Getter
@Setter
public class PlayListTrackInfoDto extends TrackInfoSaveDto {


    // 순서
    @NotNull
    private Integer order;
}

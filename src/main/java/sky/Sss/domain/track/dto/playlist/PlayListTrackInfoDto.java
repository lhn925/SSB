package sky.Sss.domain.track.dto.track;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayListTrackInfoDto extends TrackInfoSaveDto {


    // 순서
    @NotBlank
    private Integer order;
}

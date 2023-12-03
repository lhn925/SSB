package sky.Sss.domain.track.dto.playlist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.domain.track.dto.track.TrackInfoSaveDto;

@Getter
@Setter
public class PlayListTrackUpdateDto {

    @NotNull
    private Long id;

    @NotNull
    private Long trackId;

    // 순서
    @NotNull
    private Integer order;
}

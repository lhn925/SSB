package sky.Sss.domain.track.dto.playlist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.domain.track.dto.track.TrackInfoSaveDto;

@Getter
@Setter
public class PlayListTrackDeleteDto {
    @NotNull
    private Long id;

    @NotBlank
    private String token;

}

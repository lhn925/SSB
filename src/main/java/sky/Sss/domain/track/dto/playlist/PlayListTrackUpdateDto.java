package sky.Sss.domain.track.dto.playlist;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayListTrackUpdateDto {

    @NotNull
    private Long id;

    @NotNull
    private Long trackId;

    // 순서
    private Long parentId;

    private Long childId;

    private Integer position;

}

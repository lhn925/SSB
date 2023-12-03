package sky.Sss.domain.track.dto.playlist;


import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.domain.track.dto.BaseTrackDto;

@Getter
@Setter
public class PlayListSettingSaveDto extends BaseTrackDto {
    // 플레이리스트 타입
    @NotBlank
    private String playListType;

    private List<PlayListTrackInfoDto> playListTrackInfoDtoList;

}

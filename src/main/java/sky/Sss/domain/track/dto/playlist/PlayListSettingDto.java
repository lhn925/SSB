package sky.Sss.domain.track.dto.playlist;


import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.domain.track.dto.BaseTrackDto;
import sky.Sss.domain.track.model.PlayListType;

@Getter
@Setter
public class PlayListSettingDto extends BaseTrackDto {
    // 플레이리스트 타입
    @NotNull
    private PlayListType playListType;

    private List<PlayListTrackInfoDto> playListTrackInfoDtoList;

}

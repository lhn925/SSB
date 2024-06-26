package sky.Sss.domain.track.dto.playlist.req;


import jakarta.validation.Valid;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayListSettingSaveDto extends BasePlayListDto {


    @Valid
    private List<PlayListTrackInfoReqDto> playListTrackInfoDtoList;
}

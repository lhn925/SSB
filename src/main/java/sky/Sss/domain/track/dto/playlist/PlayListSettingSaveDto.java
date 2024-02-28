package sky.Sss.domain.track.dto.playlist;


import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.domain.track.dto.track.BasePlayListDto;

@Getter
@Setter
public class PlayListSettingSaveDto extends BasePlayListDto {


    private List<PlayListTrackInfoReqDto> playListTrackInfoDtoList;

}

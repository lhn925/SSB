package sky.Sss.domain.track.dto.temp.req;


import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.domain.track.dto.temp.req.TempTrackDeleteDto;

@Getter
@Setter
public class TempTracksDeleteDto {
    List<TempTrackDeleteDto> tempTrackDeleteList;
}

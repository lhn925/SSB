package sky.Sss.domain.track.dto.track.rep;


import static lombok.AccessLevel.PROTECTED;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.user.dto.rep.UserProfileHeaderDto;




@Getter
@Setter(value = AccessLevel.PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class TrackInfoListDto {

    private List<TrackDetailDto> tracks;

    private List<UserProfileHeaderDto> users;

    public TrackInfoListDto(List<TrackDetailDto> tracks, List<UserProfileHeaderDto> users) {
        this.tracks = tracks;
        this.users = users;
    }
}

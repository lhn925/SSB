package sky.Sss.domain.track.dto.track.rep;

import static lombok.AccessLevel.PROTECTED;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.dto.common.rep.BaseSearchInfoDto;
import sky.Sss.domain.track.dto.track.common.TrackInfoSimpleDto;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class TrackSearchInfoDto extends BaseSearchInfoDto {
    private TrackInfoSimpleDto trackInfo;
    private Integer playCount;
}

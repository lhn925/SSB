package sky.Sss.domain.track.dto.track.rep;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.domain.track.dto.track.common.TrackInfoSimpleDto;

@Getter
@Setter(AccessLevel.PRIVATE)
public class TrackSearchInfoDto extends TrackInfoSimpleDto {
    private Integer likeCount;
    private Integer replyCount;
    private Integer repostCount;
    private Integer playCount;
}

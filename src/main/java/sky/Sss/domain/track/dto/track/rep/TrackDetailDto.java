package sky.Sss.domain.track.dto.track.rep;

import static lombok.AccessLevel.PROTECTED;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.dto.common.rep.BaseDetailDto;
import sky.Sss.domain.track.dto.track.common.TrackInfoSimpleDto;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class TrackDetailDto extends BaseDetailDto {
    private TrackInfoSimpleDto trackInfo;
    private Integer playCount;
    public TrackDetailDto(TrackInfoSimpleDto trackInfoSimpleDto,Integer likeCount, Integer replyCount, Integer repostCount,
        Integer playCount) {
        super(likeCount, replyCount, repostCount);
        this.trackInfo = trackInfoSimpleDto;
        this.playCount = playCount;
    }
}

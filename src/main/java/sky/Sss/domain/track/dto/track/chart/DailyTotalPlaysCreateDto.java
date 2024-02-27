package sky.Sss.domain.track.dto.track.chart;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.entity.track.SsbTrack;


/**
 *
 * SsbTrackDailyTotalPlays 테이블 생성 dto
 */
@Getter
@Setter(AccessLevel.PRIVATE)
@AllArgsConstructor
public class DailyTotalPlaysCreateDto {
    private SsbTrack ssbTrack;
    private Long totalCount;

}

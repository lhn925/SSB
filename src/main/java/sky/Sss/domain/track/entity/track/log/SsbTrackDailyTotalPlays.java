package sky.Sss.domain.track.entity.track.log;


import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.dto.track.chart.DailyTotalPlaysCreateDto;
import sky.Sss.domain.track.dto.track.chart.HourlyChartPlaysDto;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.global.base.BaseTimeEntity;

/**
 * 13시를 기준으로
 * <p>
 * 전날 12시~(당일)12시의 총 track 의 조회수 모아놓은 테이블
 */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class SsbTrackDailyTotalPlays extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "track_id")
    private SsbTrack ssbTrack;

    private Integer dayTime;

    private Long totalCount;

    public static SsbTrackDailyTotalPlays create(DailyTotalPlaysCreateDto createDto, int dayTime) {
        SsbTrackDailyTotalPlays ssbTrackHourlyTotalPlays = new SsbTrackDailyTotalPlays();
        ssbTrackHourlyTotalPlays.setSsbTrack(createDto.getSsbTrack());
        ssbTrackHourlyTotalPlays.setTotalCount(createDto.getTotalCount());
        ssbTrackHourlyTotalPlays.setDayTime(dayTime);
        return ssbTrackHourlyTotalPlays;
    }
}

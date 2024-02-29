package sky.Sss.domain.track.entity.track.log;


import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.dto.track.chart.HourlyChartPlaysDto;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.global.base.BaseTimeEntity;

/**
 *
 * 시간대별 공식 조회수를 모아놓은 테이블
 *
 */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class SsbTrackHourlyTotalPlays extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "track_id")
    private SsbTrack ssbTrack;

    private Integer dayTime;

    // 지난 한시간 총 조회수
    private Long totalCount;


    public static SsbTrackHourlyTotalPlays create (HourlyChartPlaysDto hourlyChartPlaysDto) {
        SsbTrackHourlyTotalPlays ssbTrackHourlyTotalPlays = new SsbTrackHourlyTotalPlays();
        ssbTrackHourlyTotalPlays.setSsbTrack(hourlyChartPlaysDto.getSsbTrack());
        ssbTrackHourlyTotalPlays.setDayTime(hourlyChartPlaysDto.getDayTime());
        ssbTrackHourlyTotalPlays.setTotalCount(hourlyChartPlaysDto.getHourCount());
        return ssbTrackHourlyTotalPlays;
    }

}

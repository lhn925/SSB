package sky.Sss.domain.track.entity.chart;


import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.dto.track.chart.TrackTotalPlaysDto;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.global.base.BaseTimeEntity;

/**
 * 최근 24시간 까지의 순위를 저장
 * 최근 실시간 차트를 나타내는 Table
 * <p>
 * 1~500위 까지의 차트
 */
@Entity
@Getter
@Setter(value = AccessLevel.PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class SsbChartHourly extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "track_id")
    private SsbTrack ssbTrack;

    // 이전 랭킹
    private Integer prevRanking;

    private Integer ranking;

    private Integer dayTime;

    // 소수점 두번째 자리까지만
    // 지난 24시간 50% + 현시간 50%
    private Double score;

    public static SsbChartHourly create(TrackTotalPlaysDto trackTotalPlaysDto, double score) {
        SsbChartHourly ssbChartHourly = new SsbChartHourly();

        ssbChartHourly.setSsbTrack(trackTotalPlaysDto.getSsbTrack());
        ssbChartHourly.setScore(score);
        ssbChartHourly.setDayTime(trackTotalPlaysDto.getDayTime());
        ssbChartHourly.setPrevRanking(trackTotalPlaysDto.getPrevRanking()!= null ? trackTotalPlaysDto.getPrevRanking() : 0);
        return ssbChartHourly;
    }

    public static void updateRanking(SsbChartHourly ssbChartHourly,int ranking) {
        ssbChartHourly.setRanking(ranking);
    }

}

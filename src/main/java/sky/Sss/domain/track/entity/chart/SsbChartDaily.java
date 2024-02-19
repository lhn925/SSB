package sky.Sss.domain.track.entity.chart;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import sky.Sss.domain.track.dto.track.chart.TrackTotalPlaysDto;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.global.base.BaseTimeEntity;


/**
 * 일간차트 최대 500위 까지
 * 최근 24시간 이용량을 기준으로 순위를 매긴다
 */
@Entity
@Getter
@Slf4j
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SsbChartDaily extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "track_id")
    private SsbTrack ssbTrack;

    private Integer prevRanking;
    private Integer ranking;
    // 날짜 2024021614 YYYYMMddHH 형식
    private Integer dayTime;

    private Long totalCount;


    public static SsbChartDaily create(TrackTotalPlaysDto trackTotalPlaysDto) {
        SsbChartDaily ssbChartDaily = new SsbChartDaily();
        ssbChartDaily.setSsbTrack(trackTotalPlaysDto.getSsbTrack());
        ssbChartDaily.setDayTime(trackTotalPlaysDto.getDayTime());
        ssbChartDaily.setTotalCount(trackTotalPlaysDto.getTotalCount());
        ssbChartDaily.setPrevRanking(trackTotalPlaysDto.getPrevRanking()!= null ? trackTotalPlaysDto.getPrevRanking() : 0);
        return ssbChartDaily;
    }

    public static void updateRanking(SsbChartDaily ssbChartDaily, int ranking) {
        ssbChartDaily.setRanking(ranking);
    }
}

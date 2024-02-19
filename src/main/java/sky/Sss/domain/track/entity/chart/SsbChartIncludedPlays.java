package sky.Sss.domain.track.entity.chart;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.log.SsbTrackAllPlayLogs;
import sky.Sss.global.base.BaseTimeEntity;
import sky.Sss.global.utili.DayTime;

/**
 * 차트에 반영되는 플레이 수
 */
@Entity
@Setter(AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SsbChartIncludedPlays extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    @JoinColumn(name = "log_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private SsbTrackAllPlayLogs ssbTrackAllPlayLogs;
    // 시간대

    @JoinColumn(name = "track_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private SsbTrack ssbTrack;


    @Column(nullable = false)
    private Integer dayTime;

    public static SsbChartIncludedPlays create(SsbTrackAllPlayLogs ssbTrackAllPlayLogs) {
        SsbChartIncludedPlays ssbChartIncludedPlays = new SsbChartIncludedPlays();
        ssbChartIncludedPlays.setSsbTrackAllPlayLogs(ssbTrackAllPlayLogs);
        ssbChartIncludedPlays.setSsbTrack(ssbTrackAllPlayLogs.getSsbTrack());
        int dayTime = DayTime.getDayTime(ssbTrackAllPlayLogs.getCreatedDateTime());
        ssbChartIncludedPlays.setDayTime(dayTime);
        return ssbChartIncludedPlays;
    }
}

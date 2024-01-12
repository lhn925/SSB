package sky.Sss.domain.track.entity.chart;


import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.model.Hour;
import sky.Sss.global.base.BaseTimeEntity;

/**
 *
 * 차트에 반영되는 플레이
 */
@Entity
@Setter(AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SsbChartIncludedPlays extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "log_id")
    private SsbTrackAllPlayLogs ssbTrackAllPlayLogs;
    // 시간대
    private Integer hour;

    public static SsbChartIncludedPlays create(SsbTrackAllPlayLogs ssbTrackAllPlayLogs) {
        SsbChartIncludedPlays ssbChartIncludedPlays = new SsbChartIncludedPlays();
        ssbChartIncludedPlays.setSsbTrackAllPlayLogs(ssbTrackAllPlayLogs);
        return ssbChartIncludedPlays;
    }
}

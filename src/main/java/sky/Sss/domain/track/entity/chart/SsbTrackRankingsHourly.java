package sky.Sss.domain.track.entity.chart;


import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.global.base.BaseTimeEntity;

/**
 *
 * 실시간 (1시간) 차트를 나타내는 Table
 */
@Entity
@Getter
@Setter(value = AccessLevel.PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class SsbTrackRankingsHourly extends BaseTimeEntity {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(unique = true, name = "track_id")
    private SsbTrack ssbTrack;

    private Integer ranking;

    private LocalDate rankingDate;

    private Integer hour;

    // 지난 24시간 50% + 현시간 50%
    private Double score;

    // 총 조회수
    private Long totalCount;
}

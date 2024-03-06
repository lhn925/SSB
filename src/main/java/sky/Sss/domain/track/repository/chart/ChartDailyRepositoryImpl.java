package sky.Sss.domain.track.repository.chart;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.chart.SsbChartDaily;
import sky.Sss.domain.track.entity.playList.SsbPlayListTracks;
import sky.Sss.domain.track.repository.track.JdbcRepository;


@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChartDailyRepositoryImpl implements JdbcRepository<SsbChartDaily> {


    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void saveAll(List<SsbChartDaily> ssbChartDailyList, LocalDateTime createdDateTime) {
        jdbcTemplate.batchUpdate(
            "INSERT INTO ssb_chart_daily "
                + "(track_id, prev_ranking, ranking ,day_time, total_count,created_date_time , last_modified_date_time) "
                + "VALUES (?, ?,?,?,?,?,?)",
            ssbChartDailyList, 50,
            (PreparedStatement ps, SsbChartDaily ssbChartDaily) -> {
                ps.setLong(1, ssbChartDaily.getSsbTrack().getId());
                ps.setInt(2, ssbChartDaily.getPrevRanking());
                ps.setInt(3, ssbChartDaily.getRanking());
                ps.setInt(4, ssbChartDaily.getDayTime());
                ps.setLong(5, ssbChartDaily.getTotalCount());
                ps.setTimestamp(6, Timestamp.valueOf(createdDateTime));
                ps.setTimestamp(7, Timestamp.valueOf(createdDateTime));
            });
    }

    @Override
    @Transactional
    public void save(SsbChartDaily entity) {
    }
}

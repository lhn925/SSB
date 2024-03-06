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
import sky.Sss.domain.track.entity.chart.SsbChartHourly;
import sky.Sss.domain.track.repository.track.JdbcRepository;


@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChartHourlyRepositoryImpl implements JdbcRepository<SsbChartHourly> {


    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void saveAll(List<SsbChartHourly> ssbChartHourlyList, LocalDateTime createdDateTime) {
        jdbcTemplate.batchUpdate(
            "INSERT INTO ssb_chart_hourly "
                + "(track_id, prev_ranking, ranking ,day_time, score,created_date_time , last_modified_date_time) "
                + "VALUES (?, ?,?,?,?,?,?)",
            ssbChartHourlyList, 50,
            (PreparedStatement ps, SsbChartHourly ssbChartHourly) -> {
                ps.setLong(1, ssbChartHourly.getSsbTrack().getId());
                ps.setInt(2, ssbChartHourly.getPrevRanking());
                ps.setInt(3, ssbChartHourly.getRanking());
                ps.setInt(4, ssbChartHourly.getDayTime());
                ps.setDouble(5, ssbChartHourly.getScore());
                ps.setTimestamp(6, Timestamp.valueOf(createdDateTime));
                ps.setTimestamp(7, Timestamp.valueOf(createdDateTime));
            });
    }

    @Override
    @Transactional
    public void save(SsbChartHourly entity) {
    }
}

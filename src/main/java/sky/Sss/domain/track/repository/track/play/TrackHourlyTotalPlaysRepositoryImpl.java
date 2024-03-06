package sky.Sss.domain.track.repository.track.play;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.track.chart.DailyPlaysSearchDto;
import sky.Sss.domain.track.dto.track.chart.DailyTotalPlaysCreateDto;
import sky.Sss.domain.track.dto.track.chart.TrackTotalPlaysDto;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.log.SsbTrackHourlyTotalPlays;
import sky.Sss.domain.track.repository.track.JdbcRepository;


@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrackHourlyTotalPlaysRepositoryImpl implements JdbcRepository<SsbTrackHourlyTotalPlays> {

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void saveAll(List<SsbTrackHourlyTotalPlays> ssbTrackHourlyTotalPlaysList, LocalDateTime createdDateTime) {
        jdbcTemplate.batchUpdate(
            "INSERT INTO ssb_track_hourly_total_plays "
                + "(track_id,day_time,total_count, created_date_time, last_modified_date_time) VALUES (?,?,?,?,?)",
            ssbTrackHourlyTotalPlaysList, 100,
            (PreparedStatement ps, SsbTrackHourlyTotalPlays totalPlays) -> {
                ps.setLong(1, totalPlays.getSsbTrack().getId());
                ps.setInt(2, totalPlays.getDayTime());
                ps.setLong(3, totalPlays.getTotalCount());
                ps.setTimestamp(4, Timestamp.valueOf(createdDateTime));
                ps.setTimestamp(5, Timestamp.valueOf(createdDateTime));
            });
    }

    @Override
    @Transactional
    public void save(SsbTrackHourlyTotalPlays entity) {

    }
}

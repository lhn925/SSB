package sky.Sss.domain.track.repository.playList;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.playList.SsbPlayListTracks;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.repository.track.JdbcRepository;
import sky.Sss.domain.track.repository.track.TrackJpaRepository;


@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlyTracksRepositoryImpl implements JdbcRepository<SsbPlayListTracks> {


    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void saveAll(List<SsbPlayListTracks> ssbPlayListTracksList, LocalDateTime createdDateTime) {
        jdbcTemplate.batchUpdate(
            "INSERT INTO ssb_play_list_tracks "
                + "(settings_id, track_id, orders , created_date_time , last_modified_date_time) "
                + "VALUES (?, ?, ?,?,?)",
            ssbPlayListTracksList, 50,
            (PreparedStatement ps, SsbPlayListTracks ssbPlayListTracks) -> {
                ps.setLong(1, ssbPlayListTracks.getSsbPlayListSettings().getId());
                ps.setLong(2, ssbPlayListTracks.getSsbTrack().getId());
                ps.setInt(3, ssbPlayListTracks.getOrders());
                ps.setTimestamp(4, Timestamp.valueOf(createdDateTime));
                ps.setTimestamp(5, Timestamp.valueOf(createdDateTime));
            });
    }

    @Override
    @Transactional
    public void save(SsbPlayListTracks entity) {
    }
}

package sky.Sss.domain.feed.repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.feed.entity.SsbFeed;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.repository.track.JdbcRepository;
import sky.Sss.domain.track.repository.track.TrackJpaRepository;


@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedRepositoryImpl implements JdbcRepository<SsbFeed> {
    private final JdbcTemplate jdbcTemplate;
    @Override
    @Transactional
    public void saveAll(List<SsbFeed> ssbFeedList,LocalDateTime createdDateTime) {
        jdbcTemplate.batchUpdate(
            "INSERT INTO ssb_feed (uid,contents_id,contents_type,release_date_time,created_date_time,last_modified_date_time) VALUES (?,?,?,?,?,?)",
            ssbFeedList, 50,
            (PreparedStatement ps, SsbFeed ssbFeed) -> {
                ps.setLong(1,ssbFeed.getUser().getId());
                ps.setLong(2,ssbFeed.getContentsId());
                ps.setString(3,ssbFeed.getContentsType().toString());
                ps.setTimestamp(4, Timestamp.valueOf(ssbFeed.getReleaseDateTime()));
                ps.setTimestamp(5, Timestamp.valueOf(createdDateTime));
                ps.setTimestamp(6, Timestamp.valueOf(createdDateTime));
            });
    }
    @Override
    @Transactional
    public void save(SsbFeed entity) {
    }
}

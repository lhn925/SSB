package sky.Sss.domain.track.repository.track;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.track.SsbTrackTagLink;
import sky.Sss.domain.track.entity.track.SsbTrackTags;
import sky.Sss.domain.track.repository.track.JdbcRepository;
import sky.Sss.domain.track.repository.track.TrackTagJpaRepository;


@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrackTagLinkRepositoryImpl implements JdbcRepository<SsbTrackTagLink> {


    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void saveAll(List<SsbTrackTagLink> linkList, LocalDateTime localDateTime) {
        jdbcTemplate.batchUpdate(
            "INSERT INTO ssb_track_tag_link (track_id,tag_id,created_date_time,last_modified_date_Time) VALUES (?,?,?,?)",
            linkList, 50,
            (PreparedStatement ps, SsbTrackTagLink ssbTrackTagLink) -> {
                ps.setLong(1,ssbTrackTagLink.getSsbTrack().getId());
                ps.setLong(2,ssbTrackTagLink.getSsbTrackTags().getId());
                ps.setTimestamp(3, Timestamp.valueOf(localDateTime));
                ps.setTimestamp(4, Timestamp.valueOf(localDateTime));
            });
    }

    @Override
    @Transactional
    public void save(SsbTrackTagLink entity) {
    }
}

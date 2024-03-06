package sky.Sss.domain.track.repository.track;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.SsbTrackTags;


@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrackTagRepositoryImpl implements JdbcRepository<SsbTrackTags> {


    private final TrackTagJpaRepository trackTagJpaRepository;
    private final JdbcTemplate jdbcTemplate;


    public Optional<SsbTrackTags> findByTag(String tag) {
        return trackTagJpaRepository.findByTag(tag);
    }

    public List<SsbTrackTags> findAllByTagIn(Set<String> tagList) {
        return trackTagJpaRepository.findAllByTagIn(tagList);
    }




    @Override
    @Transactional
    public void saveAll(List<SsbTrackTags> tags,LocalDateTime localDateTime) {
        jdbcTemplate.batchUpdate(
            "INSERT INTO ssb_track_tags (tag,created_date_time,last_modified_date_Time) VALUES (?,?,?)",
            tags, 50,
            (PreparedStatement ps, SsbTrackTags ssbTrackTags) -> {
                ps.setString(1, ssbTrackTags.getTag());
                ps.setTimestamp(2, Timestamp.valueOf(localDateTime));
                ps.setTimestamp(3, Timestamp.valueOf(localDateTime));
            });
    }

    @Override
    @Transactional
    public void save(SsbTrackTags entity) {
        trackTagJpaRepository.save(entity);
    }
}

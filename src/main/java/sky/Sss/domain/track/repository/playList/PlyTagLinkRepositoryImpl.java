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
import sky.Sss.domain.track.entity.playList.SsbPlayListTagLink;
import sky.Sss.domain.track.entity.track.SsbTrackTagLink;
import sky.Sss.domain.track.repository.track.JdbcRepository;


@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlyTagLinkRepositoryImpl implements JdbcRepository<SsbPlayListTagLink> {


    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void saveAll(List<SsbPlayListTagLink> linkList, LocalDateTime localDateTime) {
        jdbcTemplate.batchUpdate(
            "INSERT INTO ssb_play_list_tag_link (settings_id,tag_id,created_date_time,last_modified_date_Time) VALUES (?,?,?,?)",
            linkList, 50,
            (PreparedStatement ps, SsbPlayListTagLink ssbTrackTagLink) -> {
                ps.setLong(1,ssbTrackTagLink.getSsbPlayListSettings().getId());
                ps.setLong(2,ssbTrackTagLink.getSsbTrackTags().getId());
                ps.setTimestamp(3, Timestamp.valueOf(localDateTime));
                ps.setTimestamp(4, Timestamp.valueOf(localDateTime));
            });
    }

    @Override
    @Transactional
    public void save(SsbPlayListTagLink entity) {
    }
}

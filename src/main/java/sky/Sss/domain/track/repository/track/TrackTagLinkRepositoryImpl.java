package sky.Sss.domain.track.repository.track;

import io.lettuce.core.RedisCommandExecutionException;
import java.sql.BatchUpdateException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.track.SsbTrackTagLink;
import sky.Sss.domain.track.entity.track.SsbTrackTags;
import sky.Sss.domain.track.repository.track.JdbcRepository;
import sky.Sss.domain.track.repository.track.TrackTagJpaRepository;
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisQueryService;


@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrackTagLinkRepositoryImpl implements JdbcRepository<SsbTrackTagLink> {


    private final JdbcTemplate jdbcTemplate;
    private final RedisQueryService redisQueryService;

    @Override
    @Transactional
    public void saveAll(List<SsbTrackTagLink> linkList, LocalDateTime localDateTime) {

        try {
            jdbcTemplate.batchUpdate(
                "INSERT INTO ssb_track_tag_link (track_id,tag_id,created_date_time,last_modified_date_Time) VALUES (?,?,?,?)",
                linkList, 50,
                (PreparedStatement ps, SsbTrackTagLink ssbTrackTagLink) -> {
                    ps.setLong(1,ssbTrackTagLink.getSsbTrack().getId());
                    ps.setLong(2,ssbTrackTagLink.getSsbTrackTags().getId());
                    ps.setTimestamp(3, Timestamp.valueOf(localDateTime));
                    ps.setTimestamp(4, Timestamp.valueOf(localDateTime));
                });
        }catch (DataIntegrityViolationException e) {
            // 테스트 도중 Redis 와 DB 사이에 태그 데이터 차이가 날 경우
            // 외래키 제약조건 예외 발생 처리
            redisQueryService.delete(RedisKeyDto.REDIS_TAGS_KEY);
            log.info("e.getMessage() = {}", e.getMessage());

        }

    }

    @Override
    @Transactional
    public void save(SsbTrackTagLink entity) {
    }
}

package sky.Sss.domain.track.repository.playList;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.playList.SsbPlayListTagLink;
import sky.Sss.domain.track.entity.track.SsbTrackTagLink;
import sky.Sss.domain.track.repository.track.JdbcRepository;
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisQueryService;


@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlyTagLinkRepositoryImpl implements JdbcRepository<SsbPlayListTagLink> {


    private final JdbcTemplate jdbcTemplate;
    private final RedisQueryService redisQueryService;
    @Override
    @Transactional
    public void saveAll(List<SsbPlayListTagLink> linkList, LocalDateTime localDateTime) {
        try {

            jdbcTemplate.batchUpdate(
                "INSERT INTO ssb_play_list_tag_link (settings_id,tag_id,created_date_time,last_modified_date_Time) VALUES (?,?,?,?)",
                linkList, 50,
                (PreparedStatement ps, SsbPlayListTagLink ssbTrackTagLink) -> {
                    ps.setLong(1, ssbTrackTagLink.getSsbPlayListSettings().getId());
                    ps.setLong(2, ssbTrackTagLink.getSsbTrackTags().getId());
                    ps.setTimestamp(3, Timestamp.valueOf(localDateTime));
                    ps.setTimestamp(4, Timestamp.valueOf(localDateTime));
                });
        } catch (DataIntegrityViolationException e) {
            // 테스트 도중 Redis 와 DB 사이에 태그 데이터 차이가 날 경우
            // 외래키 제약조건 예외 발생 처리
            redisQueryService.delete(RedisKeyDto.REDIS_TAGS_KEY);
            e.printStackTrace();
        }
    }

    @Override
    @Transactional
    public void save(SsbPlayListTagLink entity) {
    }
}

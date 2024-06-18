package sky.Sss.domain.track.repository.track;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.global.redis.dto.RedisKeyDto;


@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrackRepositoryImpl implements JdbcRepository<SsbTrack> {


    private final TrackJpaRepository trackJpaRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional


    @Caching(evict = {
        @CacheEvict(value = {RedisKeyDto.REDIS_USER_TOTAL_LENGTH_MAP_KEY}, key = "#ssbTrackList[0].user.userId"),
        @CacheEvict(value = RedisKeyDto.REDIS_USER_TRACK_UPLOAD_COUNT, key = "#ssbTrackList[0].user.userId"),
        @CacheEvict(value = RedisKeyDto.REDIS_USER_MY_TRACK_UPLOAD_COUNT, key = "#ssbTrackList[0].user.userId")
    })

    public void saveAll(List<SsbTrack> ssbTrackList, LocalDateTime createdDateTime) {
        jdbcTemplate.batchUpdate(
            "INSERT INTO ssb_track (cover_url, created_date_time, description, genre, is_download, is_privacy, is_status, "
                +
                "last_modified_date_time, main_genre_type, original_name, size, store_file_name, title, token, " +
                "track_length, uid, is_release) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            ssbTrackList, 50,
            (PreparedStatement ps, SsbTrack ssbTrack) -> {
                ps.setString(1, ssbTrack.getCoverUrl());
                ps.setTimestamp(2, Timestamp.valueOf(createdDateTime)); // created_date 설정
                ps.setString(3, ssbTrack.getDescription());
                ps.setString(4, ssbTrack.getGenre());
                ps.setBoolean(5, ssbTrack.getIsDownload());
                ps.setBoolean(6, ssbTrack.getIsPrivacy());
                ps.setBoolean(7, ssbTrack.getIsStatus());
                ps.setTimestamp(8, Timestamp.valueOf(createdDateTime)); // last_modified_date 설정
                ps.setString(9, ssbTrack.getMainGenreType().toString()); // Enum 타입을 String으로 변환합니다.
                ps.setString(10, ssbTrack.getOriginalName());
                ps.setLong(11, ssbTrack.getSize());
                ps.setString(12, ssbTrack.getStoreFileName());
                ps.setString(13, ssbTrack.getTitle());
                ps.setString(14, ssbTrack.getToken());
                ps.setInt(15, ssbTrack.getTrackLength());
                ps.setLong(16, ssbTrack.getUser().getId()); // User 엔티티의 ID를 참조합니다.
                ps.setBoolean(17, ssbTrack.getIsRelease());
            });
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = {RedisKeyDto.REDIS_USER_TOTAL_LENGTH_MAP_KEY}, key = "#entity.user.userId"),
        @CacheEvict(value = RedisKeyDto.REDIS_USER_TRACK_UPLOAD_COUNT, key = "#entity.user.userId"),
        @CacheEvict(value = RedisKeyDto.REDIS_USER_MY_TRACK_UPLOAD_COUNT, key = "#entity.user.userId")
    })
    public void save(SsbTrack entity) {
        trackJpaRepository.saveAndFlush(entity);
    }
}

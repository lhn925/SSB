package sky.Sss.domain.track.repository.track.play;


import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.track.dto.track.redis.RedisPlayLogDto;
import sky.Sss.domain.track.entity.track.log.SsbTrackAllPlayLogs;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.model.ChartStatus;
import sky.Sss.domain.track.model.PlayStatus;
import sky.Sss.domain.user.entity.User;

public interface TrackAllPlayLogRepository extends JpaRepository<SsbTrackAllPlayLogs, Long> {


    @Query("select s from SsbTrackAllPlayLogs s "
        + "join fetch s.ssbTrack "
        + "where s.token =:token "
        + "and s.user =:user "
        + "and s.ssbTrack =:ssbTrack "
        + "and s.chartStatus = :chartStatus")
    Optional<SsbTrackAllPlayLogs> findOne(@Param("token") String token, @Param("user") User user,
        @Param("ssbTrack")
        SsbTrack ssbTrack, @Param("chartStatus") ChartStatus chartStatus);

    @Query("select s from SsbTrackAllPlayLogs s "
        + "join fetch s.ssbTrack "
        + "where s.token =:token "
        + "and s.user =:user "
        + "and s.ssbTrack =:ssbTrack "
        + "and s.playStatus =:playStatus "
    )
    Optional<SsbTrackAllPlayLogs> findOne(@Param("token") String token, @Param("user") User user,
        @Param("ssbTrack")
        SsbTrack ssbTrack, @Param("playStatus") PlayStatus playStatus);

    @Query(
        "select s from SsbTrackAllPlayLogs s join fetch s.ssbTrack where s.ssbTrack.id = :trackId  and s.token =:playToken ")
    Optional<SsbTrackAllPlayLogs> findOne(@Param("trackId") long trackId, @Param("playToken") String playToken);


    @Query("select new sky.Sss.domain.track.dto.track.redis.RedisPlayLogDto(s.token,t.id,t.storeFileName,t.token,s.expireTime,s.user.id)"
        + " from SsbTrackAllPlayLogs s "
        + " join SsbTrack t on s.ssbTrack = t"
        + " where t.token in (:trackTokens) "
        + " and s.playStatus =:playStatus "
    )
    List<RedisPlayLogDto> getRedisPlayLogDtoList (@Param("trackTokens") Set<String> trackTokens, @Param("playStatus") PlayStatus playStatus);

}

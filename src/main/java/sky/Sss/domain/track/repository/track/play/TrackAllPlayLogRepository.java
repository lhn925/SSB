package sky.Sss.domain.track.repository.track.play;


import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.track.entity.track.log.SsbTrackAllPlayLogs;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.model.ChartStatus;
import sky.Sss.domain.track.model.PlayStatus;
import sky.Sss.domain.user.entity.User;

public interface TrackAllPlayLogRepository extends JpaRepository<SsbTrackAllPlayLogs, Long> {


    @Query("select s from SsbTrackAllPlayLogs s where s.id = :id and s.token =:token "
        + "and s.user =:user "
        + "and s.ssbTrack =:ssbTrack "
        + "and s.chartStatus = :chartStatus")
    Optional<SsbTrackAllPlayLogs> findOne(@Param("id") Long id, @Param("token") String token, @Param("user") User user,
        @Param("ssbTrack")
        SsbTrack ssbTrack, @Param("chartStatus") ChartStatus chartStatus);

    @Query("select s from SsbTrackAllPlayLogs s where s.id = :id and s.token =:token "
        + "and s.user =:user "
        + "and s.ssbTrack =:ssbTrack "
        + "and s.playStatus =:playStatus "
    )
    Optional<SsbTrackAllPlayLogs> findOne(@Param("id") Long id, @Param("token") String token, @Param("user") User user,
        @Param("ssbTrack")
        SsbTrack ssbTrack, @Param("playStatus") PlayStatus playStatus);

    @Query(
        "select s from SsbTrackAllPlayLogs s join fetch SsbTrack t where s.ssbTrack.id = :trackId  and s.token =:playToken ")
    Optional<SsbTrackAllPlayLogs> findOne(@Param("trackId") long trackId, @Param("playToken") String playToken);


}

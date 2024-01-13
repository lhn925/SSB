package sky.Sss.domain.track.repository.track;


import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.track.entity.chart.SsbChartIncludedPlays;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.user.entity.User;

public interface TrackPlayCountRepository extends JpaRepository<SsbChartIncludedPlays, Long> {

    /**
     * 회원 플레이 여부 조회
     *
     * @param ssbTrack
     * @param playDateTime
     * @return
     */
    // 특정 시간 대에 플레이 여부 조회
    @Query(
        "select c from SsbChartIncludedPlays c where c.user = :user and c.ssbTrack = :ssbTrack and DATE_FORMAT(c.createdDateTime,'%Y-%m-%d %H:00:00') = "
            + "DATE_FORMAT(:playDateTime,'%Y-%m-%d %H:00:00')")
    Optional<SsbChartIncludedPlays> checkSongPlayAtTime(@Param("user") User user, @Param("ssbTrack") SsbTrack ssbTrack,
        @Param("playDateTime") LocalDateTime playDateTime);


//    /**
//     * 비 회원 플레이 여부 조회
//     *
//     * @param uid
//     * @param ssbTrack
//     * @param playDateTime
//     * @return
//     */
//    // 특정 시간 대에 플레이 여부 조회
//    @Query("select c from SsbChartIncludedPlays c where c.ssbTrack = :ssbTrack and (c.sessionId =:sessionId or c.defaultLocationLog.ip = :ip) and DATE_FORMAT(c.createdDateTime,'%Y-%m-%d %H:00:00') = "
//        + "DATE_FORMAT(:playDateTime,'%Y-%m-%d %H:00:00')")
//    SsbChartIncludedPlays checkSongPlayAtTimeNotMember(@Param("uid") Long uid, @Param("ssbTrack") SsbTrack ssbTrack,
//        @Param("playDateTime") LocalDateTime playDateTime);

}

package sky.Sss.domain.track.repository.track;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.track.entity.chart.SsbChartIncludedPlays;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.model.ChartStatus;
import sky.Sss.domain.track.model.PlayStatus;
import sky.Sss.domain.user.entity.User;

public interface TrackChartIncludedRepository extends JpaRepository<SsbChartIncludedPlays, Long> {


    // 특정 시간 대에 플레이 여부 조회
    @Query(
        "select c from SsbChartIncludedPlays c join fetch c.ssbTrackAllPlayLogs where "
            + " c.ssbTrackAllPlayLogs.user = :user and c.ssbTrackAllPlayLogs.ssbTrack =:ssbTrack"
            + " and c.createDate =:playDate and c.hour =:hour "
            + "and c.ssbTrackAllPlayLogs.chartStatus =:chartStatus "
            + "and c.ssbTrackAllPlayLogs.playStatus =:playStatus ")
    Optional<SsbChartIncludedPlays> checkPlayAtTime(@Param("user") User user, @Param("ssbTrack") SsbTrack ssbTrack,
        @Param("hour") Integer hour, @Param("chartStatus")
    ChartStatus chartStatus, @Param("playStatus") PlayStatus playStatus,
        @Param("playDate") LocalDate localDate);


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

package sky.Sss.domain.track.repository.chart;


import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.track.dto.track.chart.HourlyChartPlaysDto;
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
            + " and c.dayTime =:dayTime "
            + " and c.ssbTrackAllPlayLogs.chartStatus =:chartStatus "
            + " and c.ssbTrackAllPlayLogs.playStatus =:playStatus ")
    Optional<SsbChartIncludedPlays> checkPlayAtTime(@Param("user") User user, @Param("ssbTrack") SsbTrack ssbTrack,
        @Param("dayTime") int dayTime, @Param("chartStatus")
    ChartStatus chartStatus, @Param("playStatus") PlayStatus playStatus);

    /**
     * 지난 한시간동안의 includeChart 데이터
     *
     * @return
     */
    @Query(value =
        " select new sky.Sss.domain.track.dto.track.chart.HourlyChartPlaysDto(c.ssbTrack, count(c.id) ,c.dayTime) "
            + " from SsbChartIncludedPlays c "
            + " where c.dayTime = :dayTime group by c.ssbTrack  order by count (c.id) desc")
    List<HourlyChartPlaysDto> getHourlyChartPlays(@Param("dayTime") int dayTime);





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

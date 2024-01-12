package sky.Sss.domain.track.repository;


import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.SsbTrackPlayCounts;

public interface TrackPlayCountRepository extends JpaRepository<SsbTrackPlayCounts, Long> {

    /**
     * 회원 플레이 여부 조회
     *
     * @param uid
     * @param ssbTrack
     * @param playDateTime
     * @return
     */
    // 특정 시간 대에 플레이 여부 조회
    @Query(
        "select c from SsbTrackPlayCounts c where c.uid = :uid and c.ssbTrack = :ssbTrack and DATE_FORMAT(c.createdDateTime,'%Y-%m-%d %H:00:00') = "
            + "DATE_FORMAT(:playDateTime,'%Y-%m-%d %H:00:00')")
    SsbTrackPlayCounts checkSongPlayAtTime(@Param("uid") Long uid, @Param("ssbTrack") SsbTrack ssbTrack,
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
//    @Query("select c from SsbTrackPlayCounts c where c.ssbTrack = :ssbTrack and (c.sessionId =:sessionId or c.defaultLocationLog.ip = :ip) and DATE_FORMAT(c.createdDateTime,'%Y-%m-%d %H:00:00') = "
//        + "DATE_FORMAT(:playDateTime,'%Y-%m-%d %H:00:00')")
//    SsbTrackPlayCounts checkSongPlayAtTimeNotMember(@Param("uid") Long uid, @Param("ssbTrack") SsbTrack ssbTrack,
//        @Param("playDateTime") LocalDateTime playDateTime);

}

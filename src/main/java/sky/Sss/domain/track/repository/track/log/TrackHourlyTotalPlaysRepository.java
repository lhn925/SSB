package sky.Sss.domain.track.repository.track.log;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.track.dto.track.chart.DailyPlaysSearchDto;
import sky.Sss.domain.track.dto.track.chart.DailyTotalPlaysCreateDto;
import sky.Sss.domain.track.dto.track.chart.TrackTotalPlaysDto;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.log.SsbTrackHourlyTotalPlays;

public interface TrackHourlyTotalPlaysRepository extends JpaRepository<SsbTrackHourlyTotalPlays, Long> {


    /**
     * 지난 한시간동안의 includeChart 데이터
     * 이전 시간대의 chartData
     *
     * @return
     */
    @Query(value =
        " select new sky.Sss.domain.track.dto.track.chart.TrackTotalPlaysDto(s.ssbTrack, s.totalCount ,s.dayTime,h.ranking) "
            + " from SsbTrackHourlyTotalPlays s left outer join SsbChartHourly h "
            + " on s.ssbTrack = h.ssbTrack and h.dayTime = :prevDayTime"
            + " where s.dayTime = :dayTime order by s.totalCount desc")
    List<TrackTotalPlaysDto> getHourlyPlays(@Param("dayTime") int dayTime, @Param("prevDayTime") int prevDayTime,
        PageRequest pageRequest);

    /**
     * 최근 24시간 동안의 플레이 횟수
     *
     * @return
     */
    @Query(value =
        " select new sky.Sss.domain.track.dto.track.chart.DailyPlaysSearchDto(s.ssbTrack.id,sum(s.totalCount)) "
            + " from SsbTrackHourlyTotalPlays s "
            + " where s.dayTime between :startTime and :endTime and s.ssbTrack in :ssbTracks group by s.ssbTrack ")
    List<DailyPlaysSearchDto> getDailyPlaysAndTrackId(@Param("startTime") int startTime, @Param("endTime") int endTime,
        @Param("ssbTracks")
        List<SsbTrack> ssbTracks);

    /**
     * 최근 24시간 동안의 플레이 횟수
     *
     * @return
     */
    @Query(value =
        " select new sky.Sss.domain.track.dto.track.chart.DailyTotalPlaysCreateDto(s.ssbTrack,sum(s.totalCount)) "
            + " from SsbTrackHourlyTotalPlays s "
            + " where s.dayTime between :startTime and :endTime group by s.ssbTrack ")
    List<DailyTotalPlaysCreateDto> getDailyPlays(@Param("startTime") int startTime, @Param("endTime") int endTime);
}

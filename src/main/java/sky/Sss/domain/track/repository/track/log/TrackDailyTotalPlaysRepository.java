package sky.Sss.domain.track.repository.track.log;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.track.dto.track.chart.TrackTotalPlaysDto;
import sky.Sss.domain.track.entity.track.log.SsbTrackDailyTotalPlays;

public interface TrackDailyTotalPlaysRepository extends JpaRepository<SsbTrackDailyTotalPlays, Long> {


    /**
     *
     * @return
     */
    @Query(value =
        " select new sky.Sss.domain.track.dto.track.chart.TrackTotalPlaysDto(s.ssbTrack, s.totalCount ,s.dayTime,d.ranking) "
            + " from SsbTrackDailyTotalPlays s left outer join SsbChartDaily d "
            + " on s.ssbTrack = d.ssbTrack and d.dayTime = :prevDayTime"
            + " where s.dayTime = :dayTime order by s.totalCount desc")
    List<TrackTotalPlaysDto> getDailyPlays(@Param("dayTime") int dayTime, @Param("prevDayTime") int prevDayTime,
        PageRequest pageRequest);


}

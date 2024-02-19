package sky.Sss.domain.track.repository.chart;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sky.Sss.domain.track.entity.chart.SsbChartDaily;
import sky.Sss.domain.track.entity.chart.SsbChartHourly;


public interface ChartDailyRepository extends JpaRepository<SsbChartDaily, Long> {


    /**
     * 최근 24시간 chart 정보를 가져오는 method
     *
     * @return
     */
    List<SsbChartDaily> findByDayTime(int dayTime);

    /**
     * 해당 시간대에 차트 검색
     * @param dayTime
     * @return
     */
    boolean existsByDayTime(int dayTime);
}

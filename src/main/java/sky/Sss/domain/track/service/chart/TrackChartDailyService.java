package sky.Sss.domain.track.service.chart;


import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.chart.SsbChartDaily;
import sky.Sss.domain.track.entity.chart.SsbChartHourly;
import sky.Sss.domain.track.repository.chart.ChartDailyRepository;
import sky.Sss.domain.track.repository.chart.ChartDailyRepositoryImpl;
import sky.Sss.domain.track.repository.chart.ChartHourlyRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrackChartDailyService {


    private final ChartDailyRepository chartDailyRepository;
    private final ChartDailyRepositoryImpl chartDailyRepositoryImpl;


    @Transactional
    public void save(SsbChartDaily ssbChartDaily) {
        chartDailyRepository.save(ssbChartDaily);
    }

    @Transactional
    public void saveAll(List<SsbChartDaily> ssbChartHourly) {
        chartDailyRepositoryImpl.saveAll(ssbChartHourly, LocalDateTime.now());
    }

    public boolean checkChart(int dayTime) {
        return chartDailyRepository.existsByDayTime(dayTime);
    }
}

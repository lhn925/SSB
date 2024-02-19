package sky.Sss.domain.track.service.chart;


import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.chart.SsbChartHourly;
import sky.Sss.domain.track.repository.chart.ChartHourlyRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrackChartHourlyService {

    private final ChartHourlyRepository chartHourlyRepository;


    @Transactional
    public void save(SsbChartHourly ssbChartHourly) {
        chartHourlyRepository.save(ssbChartHourly);
    }

    @Transactional
    public void saveAll(List<SsbChartHourly> ssbChartHourly) {
        chartHourlyRepository.saveAll(ssbChartHourly);
    }

    public boolean checkChart(int dayTime) {
        return chartHourlyRepository.existsByDayTime(dayTime);
    }
}
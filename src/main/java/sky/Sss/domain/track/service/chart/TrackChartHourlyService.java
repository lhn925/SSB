package sky.Sss.domain.track.service.chart;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.chart.SsbTrackChartHourly;
import sky.Sss.domain.track.repository.chart.TrackChartHourlyRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrackChartHourlyService {

    private final TrackChartHourlyRepository trackChartHourlyRepository;


    @Transactional
    public void save(SsbTrackChartHourly ssbTrackChartHourly) {
        trackChartHourlyRepository.save(ssbTrackChartHourly);
    }
    // 조회수
}

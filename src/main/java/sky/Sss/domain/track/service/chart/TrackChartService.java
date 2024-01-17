package sky.Sss.domain.track.service.chart;


import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.chart.SsbTrackChartHourly;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.model.Hour;
import sky.Sss.domain.track.service.track.TrackQueryService;
import sky.Sss.domain.user.service.UserQueryService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrackChartService {

    private final TrackChartHourlyService trackChartHourlyService;
    private final TrackQueryService trackQueryService;
    private final TrackChatIncludedService trackChatIncludedService;
    private final UserQueryService userQueryService;

    /**
     * 1시간마다 정각에 갱신
     * 실시간 차트 계산
     * 최근 24시간 50% + 해당 시간대에 50%
     */
    @Transactional
    public void updateHourChart(LocalDate createDate, Hour hour) {
        List<SsbTrackChartHourly> ssbTrackChartHourlyList = null;


    }

}

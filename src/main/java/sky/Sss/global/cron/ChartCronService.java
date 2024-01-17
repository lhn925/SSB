package sky.Sss.global.cron;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import sky.Sss.domain.track.service.track.TrackPlaybackMetricsService;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ChartCronService {


    private final TrackPlaybackMetricsService trackPlaybackMetricsService;
    /**
     * 정각마다 이전 시간대에 조회수 총합 후
     * 실시간 차트 계산
     *
     */
    @Scheduled(cron = "0 0 0-23 * * *") //    초 - 분 - 시 - 일 - 월 - 요일
    public void rankingHour() {
        LocalDateTime localDateTime = LocalDateTime.now().minusHours(1);
        log.info("localDateTime.getHour() = {}", localDateTime.getHour());
        log.info("ChartCronService");
    }

}


package sky.Sss.global.cron;

import java.time.LocalDateTime;
import java.time.Month;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.model.Hour;
import sky.Sss.domain.track.service.track.play.TrackPlayMetricsService;
import sky.Sss.global.utili.DayTime;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ChartCronService {


    private final TrackPlayMetricsService trackPlayMetricsService;

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 정각마다 현재 시간대에 조회수 총합
     */
    // fixedDelay 이전 호출이 완료된 후 부터 시작
    @Transactional
//    @Scheduled(cron = "0 0 0-23 * * *")  //    초 - 분 - 시 - 일 - 월 - 요일
    public void hourlyTotal() {
        LocalDateTime rankingDateTime = LocalDateTime.now().minusHours(Hour.HOUR_01.getValue());
        int ranDayTime = DayTime.getDayTime(rankingDateTime);
        // track 시간대 별 종합 조회수 생성
        trackPlayMetricsService.addTrackHourlyTotalPlays(ranDayTime);
    }


    /**
     * 정각마다 일간 조회수 총합 후 생성
     * 전날 12 ~ 금일 12시까지의 데이터 총합후
     * 13 시에 업데이트
     */
    // fixedDelay 이전 호출이 완료된 후 부터 시작
    @Transactional
//    @Scheduled(cron = "2 0 13 * * *")  //    초 - 분 - 시 - 일 - 월 - 요일
    public void dailyTotal() {
        LocalDateTime rankingDateTime = LocalDateTime.now().minusHours(Hour.HOUR_01.getValue());
        // 24시간 전
        int startDayTime = DayTime.getDayTime(rankingDateTime, Hour.HOUR_24);
        // 24시간 마지막 시간대
        int endDayTime = DayTime.getDayTime(rankingDateTime);
        // track 시간대 별 종합 조회수 생성
        trackPlayMetricsService.addTrackDailyTotalPlays(startDayTime, endDayTime);
    }

    /**
     * 시간대별 차트 생성 및
     * 13시 일 경우 일간차트 생성
     * 정각마다 일간 조회수 총합 후 생성
     * 전날 12 ~ 금일 12시까지의 데이터 총합후
     * 13 시에 업데이트
     *
     */
    @Transactional
    @Scheduled(cron = "0 0 0-23 * * *")  //    초 - 분 - 시 - 일 - 월 - 요일
    public void rankingCal() {
        LocalDateTime rankingDateTime = LocalDateTime.now().minusHours(Hour.HOUR_01.getValue());

        int ranDayTime = DayTime.getDayTime(rankingDateTime);
        // 24시간 전
        int startDayTime = DayTime.getDayTime(rankingDateTime, Hour.HOUR_24);
        // 24시간 마지막 시간대
        int endDayTime = DayTime.getDayTime(rankingDateTime, Hour.HOUR_01);

        PageRequest pageRequest = PageRequest.of(0, 500);

        // 시간대별 조회수 집계
        trackPlayMetricsService.addTrackHourlyTotalPlays(ranDayTime);

        // 차트 생성
        trackPlayMetricsService.addChartHourly(ranDayTime, startDayTime, endDayTime, pageRequest);

        // 13 정각일 경우
        // 일간 차트 등록
        if (rankingDateTime.getHour() == 12) {
            trackPlayMetricsService.addTrackDailyTotalPlays(startDayTime, endDayTime);
            trackPlayMetricsService.addChartDaily(ranDayTime, startDayTime, pageRequest);
        }
    }

    /**
     * 일간 차트
     * 매일 낮 12시 기준 최근 24시간 이용량 집계 후 13시에 업데이트
     */
    @Transactional
//    @Scheduled(cron = "5 0 13 * * *")  //    초 - 분 - 시 - 일 - 월 - 요일
    public void rankingDaily() {
        LocalDateTime rankingDateTime = LocalDateTime.now().minusHours(Hour.HOUR_01.getValue());
        int dayTime = DayTime.getDayTime(rankingDateTime);
        int prevDayTime = DayTime.getDayTime(rankingDateTime, Hour.HOUR_24);
        trackPlayMetricsService.addChartDaily(dayTime, prevDayTime, PageRequest.of(0, 500));
    }

//    /**
//     * 일간 차트
//     * 매일 낮 12시 기준 최근 24시간 이용량 집계 후 13시에 업데이트
////     */
//    @Scheduled(fixedDelay = 5000L)  //    초 - 분 - 시 - 일 - 월 - 요일
//    public void test() {
//
//        log.info("==========테스트 중===========");
//        PushWebSocketDto testWebSocketDto = new PushWebSocketDto("lim222", "안녕하세요","hihihi");
//        messagingTemplate.convertAndSend("/topic/push/lim222",testWebSocketDto);
//        log.info("==========테스트 중===========");
//    }

}


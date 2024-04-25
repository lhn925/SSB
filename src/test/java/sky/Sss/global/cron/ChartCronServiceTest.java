package sky.Sss.global.cron;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import sky.Sss.domain.track.dto.track.chart.TrackTotalPlaysDto;
import sky.Sss.domain.track.model.Hour;
import sky.Sss.domain.track.service.track.play.TrackDailyTotalPlaysService;
import sky.Sss.domain.track.service.track.play.TrackPlayMetricsService;
import sky.Sss.global.utili.DayTime;

@SpringBootTest
class ChartCronServiceTest {

    @Autowired
    ChartCronService chartCronService;

    @Autowired
    TrackDailyTotalPlaysService trackDailyTotalPlaysService;


    @Autowired
    TrackPlayMetricsService trackPlayMetricsService;

    @Test
    void hourlyTotal() {
        chartCronService.hourlyTotal();
    }

    @Test
    void dailyTotal() {
        chartCronService.rankingCal();
    }

    @Test
    void rankingHour() {
        LocalDateTime rankingDateTime = LocalDateTime.of(2024, Month.APRIL, 24, 16, 0)
            .minusHours(Hour.HOUR_01.getValue());
        int ranDayTime = DayTime.getDayTime(rankingDateTime);
        // 24시간 전
        int startDayTime = DayTime.getDayTime(rankingDateTime, Hour.HOUR_24);
        // 24시간 마지막 시간대
        int endDayTime = DayTime.getDayTime(rankingDateTime, Hour.HOUR_01);
        PageRequest pageRequest = PageRequest.of(0, 500);
        trackPlayMetricsService.addChartHourly(ranDayTime, startDayTime, endDayTime, pageRequest);

    }

    @Test
    void rankingDaily() {
        chartCronService.rankingDaily();
    }


    @Test
    public void test() {
        List<TrackTotalPlaysDto> dailyTotalPlays = trackDailyTotalPlaysService.getDailyTotalPlays(2024021614,
            2024021514, PageRequest.of(0, 500));
        System.out.println("dailyTotalPlays.get(0).getSsbTrack() = " + dailyTotalPlays.get(0).getSsbTrack());
    }
}
package sky.Sss.global.cron;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import sky.Sss.domain.track.dto.track.chart.TrackTotalPlaysDto;
import sky.Sss.domain.track.service.track.play.TrackDailyTotalPlaysService;

@SpringBootTest
class ChartCronServiceTest {

    @Autowired
    ChartCronService chartCronService;

    @Autowired
    TrackDailyTotalPlaysService trackDailyTotalPlaysService;

    @Test
    void hourlyTotal() {
        chartCronService.hourlyTotal();
    }

    @Test
    void dailyTotal() {
        chartCronService.dailyTotal();
    }

    @Test
    void rankingHour() {
        chartCronService.rankingHour();
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
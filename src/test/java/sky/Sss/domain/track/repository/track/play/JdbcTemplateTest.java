package sky.Sss.domain.track.repository.track.play;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.Sss.domain.track.dto.track.chart.HourlyChartPlaysDto;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.log.SsbTrackHourlyTotalPlays;

@SpringBootTest
class JdbcTemplateTest {


    @Autowired
    TrackHourlyTotalPlaysRepositoryImpl trackHourlyTotalPlaysRepositorytest;

    @Test
    void saveAll() {

        SsbTrack ss1 = SsbTrack.builder().id(1L).build();
        HourlyChartPlaysDto hourlyChartPlaysDto1 = new HourlyChartPlaysDto(ss1, 12L, 2022122312);
        SsbTrack ss2 = SsbTrack.builder().id(2L).build();
        HourlyChartPlaysDto hourlyChartPlaysDto2 = new HourlyChartPlaysDto(ss2, 12L, 2022122312);

        SsbTrackHourlyTotalPlays ssbTrackHourlyTotalPlays1 = SsbTrackHourlyTotalPlays.create(hourlyChartPlaysDto1);
        SsbTrackHourlyTotalPlays ssbTrackHourlyTotalPlays2 = SsbTrackHourlyTotalPlays.create(hourlyChartPlaysDto2);

        List<SsbTrackHourlyTotalPlays> arrayList = new ArrayList<>();

        arrayList.add(ssbTrackHourlyTotalPlays1);
        arrayList.add(ssbTrackHourlyTotalPlays2);
        trackHourlyTotalPlaysRepositorytest.saveAll(arrayList, LocalDateTime.now());
    }
}
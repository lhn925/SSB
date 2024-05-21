package sky.Sss.domain.track.service.chart;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.Sss.domain.track.entity.chart.SsbChartIncludedPlays;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.service.track.TrackQueryService;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.global.utili.DayTime;

@SpringBootTest
class TrackChatIncludedServiceTest {

    @Autowired
    TrackChatIncludedService trackChatIncludedService;

    @Autowired
    TrackQueryService trackQueryService;

    @Autowired
    UserQueryService userQueryService;
    
    
    @Test
    public void cacheTest() {
    
    // given
        SsbTrack ssbTrack = trackQueryService.findById(9L, Status.ON);
        User user = userQueryService.findOne("lim2226");

        LocalDateTime now = LocalDateTime.now();
        boolean b = trackChatIncludedService.existsIncludeChart(user, ssbTrack, DayTime.getDayTime(now));

        System.out.println("b = " + b);

//        trackChatIncludedService.

        // when
    
    // then
    
    }

}
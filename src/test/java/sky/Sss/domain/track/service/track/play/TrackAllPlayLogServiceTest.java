package sky.Sss.domain.track.service.track.play;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.Sss.domain.track.entity.track.SsbTrackLikes;
import sky.Sss.domain.track.repository.track.TrackQueryRepository;
import sky.Sss.domain.track.repository.track.play.TrackAllPlayLogRepository;

@SpringBootTest
class TrackAllPlayLogServiceTest {


    @Autowired
    TrackAllPlayLogRepository trackAllPlayLogRepository;

    @Autowired
    TrackAllPlayLogService trackAllPlayLogService;

    @Autowired
    TrackQueryRepository trackQueryRepository;


    @Test
    public void queryTest() {

        trackQueryRepository.findById(3L);

    }


    @Test
    public void getTotalCountList() {
        List<String> keys = new ArrayList<>();
        keys.add("0aac203d6b51f3840d40");
        keys.add("22d182fe5d2d9b09667b");
        keys.add("ebb90c922de3f3082dc8");
        Map<String, Integer> totalCountList = trackAllPlayLogService.getTotalCountList(keys);
        for (String s : totalCountList.keySet()) {
            Integer integer = totalCountList.get(s);
            System.out.println("token = " + s);
            System.out.println("integer = " + integer);
        }

    }

}
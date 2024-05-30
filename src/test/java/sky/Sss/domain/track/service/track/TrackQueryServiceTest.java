package sky.Sss.domain.track.service.track;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.Sss.domain.track.entity.track.SsbTrack;

@SpringBootTest
class TrackQueryServiceTest {


    @Autowired
    TrackQueryService trackQueryService;




    @Test
    public void fetchTest() {

        Set<Long> ids = new HashSet<>();

        ids.add(1L);
        ids.add(2L);
        ids.add(3L);

        List<SsbTrack> trackListFromOrDbByIds = trackQueryService.getTrackListFromOrDbByIds(ids);

        for (SsbTrack trackListFromOrDbById : trackListFromOrDbByIds) {

            System.out.println("trackListFromOrDbById.getTitle() = " + trackListFromOrDbById.getTitle());
        }
    }

}
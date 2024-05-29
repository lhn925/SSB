package sky.Sss.domain.track.service.track.play;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.Sss.domain.track.repository.track.TrackQueryRepository;
import sky.Sss.domain.track.repository.track.play.TrackAllPlayLogRepository;

@SpringBootTest
class TrackAllPlayLogServiceTest {


    @Autowired
    TrackAllPlayLogRepository trackAllPlayLogRepository;

    @Autowired
    TrackQueryRepository trackQueryRepository;




    @Test
    public void queryTest() {

        trackQueryRepository.findById(3L);

    }

}
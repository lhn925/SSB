package sky.Sss.domain.track.repository.track;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.Sss.domain.track.dto.track.common.TrackInfoSimpleDto;

@SpringBootTest
class TrackQueryRepositoryTest {


    @Autowired
    TrackQueryRepository trackQueryRepository;

    @Test
    void getTrackInfoSimpleDtoList() {

        Set<Long> hashSet = new HashSet<>();

        hashSet.add(1L);
        hashSet.add(2L);
        hashSet.add(3L);
        List<TrackInfoSimpleDto> trackInfoSimpleDtoList = trackQueryRepository.getTrackInfoSimpleDtoList(hashSet, 3,
            true);

        for (TrackInfoSimpleDto trackInfoSimpleDto : trackInfoSimpleDtoList) {
            System.out.println("trackInfoSimpleDto.getId() = " + trackInfoSimpleDto.getId());
            System.out.println("trackInfoSimpleDto.getIsLike() = " + trackInfoSimpleDto.getIsLike());
            System.out.println("trackInfoSimpleDto.getIsOwner() = " + trackInfoSimpleDto.getIsOwner());
        }
        Assertions.assertEquals(3, trackInfoSimpleDtoList.size());
    }
}
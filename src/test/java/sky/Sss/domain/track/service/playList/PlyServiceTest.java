package sky.Sss.domain.track.service.playList;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.playlist.PlayListTrackDeleteDto;
import sky.Sss.domain.track.dto.playlist.PlayListTrackUpdateDto;
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;
import sky.Sss.domain.track.entity.playList.SsbPlayListTracks;
import sky.Sss.domain.track.repository.playList.PlyTracksRepository;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.UserQueryService;


@SpringBootTest
class PlyServiceTest {

    @Autowired
    PlyService plyService;

    @Autowired
    PlyQueryService plyQueryService;

    @Autowired
    UserQueryService userQueryService;

    PlyTracksRepository plyTracksRepository;

    // deleteLinkTracks 삭제 테스트

    User user = null;

    SsbPlayListSettings findSettings = null;
    List<SsbPlayListTracks> playListTracks = null;
    @BeforeEach
    public void before () {
        // given
         user = userQueryService.findOne("lim222");

        // playListQuery
         findSettings = plyService.findOneJoinTracks(20L, "499568da9d4dfdecd559", user, Status.ON);
        playListTracks = findSettings.getPlayListTracks();
    }

    
    @Test
    public void allTest() {

    // given
        updateTrackOrderTest();
        deleteTrackTest();
    // when
    
    // then
    
    }
    
    @Test
    @Transactional
    @Rollback(value = false)
    public void deleteTrackTest() {

        // delete List 생성
        PlayListTrackDeleteDto playListTrackDeleteDto1 = new PlayListTrackDeleteDto();
        playListTrackDeleteDto1.setId(89L);

        List<PlayListTrackDeleteDto> deleteDtoList = new ArrayList<>();

        deleteDtoList.add(playListTrackDeleteDto1);


        // when
        plyService.deleteLinkTracks(playListTracks, deleteDtoList);

        // then
        for (SsbPlayListTracks playListTrack : playListTracks) {
            System.out.println(" ============" + playListTracks.size() + "========== ");
            System.out.println("playListTrack.getId() = " + playListTrack.getId());
            System.out.println("playListTrack.getParentId() = " + playListTrack.getParentId());
            System.out.println("playListTrack.getChildId() = " + playListTrack.getChildId());
            System.out.println(" ====================== ");
        }
    }


    @Test
    @Transactional
    @Rollback(value = false)
    public void updateTrackOrderTest() {

        // update List 생성
        List<PlayListTrackUpdateDto> updateList = new ArrayList<>();

        long index = 93;
        // 정렬
//        for (int i = 0; i < playListTracks.size(); i++) {
//            PlayListTrackUpdateDto playListTrackUpdateDto = new PlayListTrackUpdateDto();
//            playListTrackUpdateDto.setId(index);
//            playListTrackUpdateDto.setParentId(i == 0 ? null : index - 1);
//            playListTrackUpdateDto.setChildId(i == (playListTracks.size() -1) ? null : index + 1);
//            index++;
//            updateList.add(playListTrackUpdateDto);
//        }

//        PlayListTrackUpdateDto playListTrackUpdateDto3 = new PlayListTrackUpdateDto();
//        playListTrackUpdateDto3.setId(99L);
//        playListTrackUpdateDto3.setParentId(97L);
//        playListTrackUpdateDto3.setChildId(95L);

        PlayListTrackUpdateDto playListTrackUpdateDto = new PlayListTrackUpdateDto();
        playListTrackUpdateDto.setId(95L);
        playListTrackUpdateDto.setParentId(96L);
        playListTrackUpdateDto.setChildId(94L);


//        PlayListTrackUpdateDto playListTrackUpdateDto2 = new PlayListTrackUpdateDto();
//        playListTrackUpdateDto2.setId(98L);
//        playListTrackUpdateDto2.setParentId(95L);
//        playListTrackUpdateDto2.setChildId(100L);
        // child 였던놈이 parent 로
//        updateList.add(playListTrackUpdateDto3);
        updateList.add(playListTrackUpdateDto);
//        updateList.add(playListTrackUpdateDto2);


        // update List 생성

        try {
            plyService.updateTrackLinked(playListTracks, updateList);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }


        // 첫 번째 배열 구하기
        SsbPlayListTracks value = playListTracks.stream().filter(ply -> ply.getParentId() == null)
            .findFirst().orElse(null);

        Map<Long, SsbPlayListTracks> map = playListTracks.stream()
            .collect(Collectors.toMap(SsbPlayListTracks::getId, ply -> ply));

        List<SsbPlayListTracks> sortedList = new ArrayList<>();
        addSortedList(value, map, sortedList);


        if (sortedList.size() != playListTracks.size()) {
            throw new RuntimeException();
        }

        for (SsbPlayListTracks ssbPlayListTracks : sortedList) {
            System.out.println(" ============" + playListTracks.size() + "========== ");
            System.out.println("playListTrack.getId() = " + ssbPlayListTracks.getId());
            System.out.println("playListTrack.getParentId() = " + ssbPlayListTracks.getParentId());
            System.out.println("playListTrack.getChildId() = " + ssbPlayListTracks.getChildId());
            System.out.println(" ====================== ");
        }

    }
    private static void addSortedList(SsbPlayListTracks value, Map<Long, SsbPlayListTracks> map,
        List<SsbPlayListTracks> sortedList) {
        if (value != null) {
            map.get(value.getId());
            sortedList.add(value);
            addSortedList(map.get(value.getChildId()), map, sortedList);
        }
    }
}
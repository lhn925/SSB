package sky.Sss.domain.track.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.Sss.domain.track.repository.playList.PlySettingRepository;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.repository.UserQueryRepository;

@SpringBootTest
class PlayListSettingRepositoryTest {


    @Autowired
    PlySettingRepository plySettingRepository;

    @Autowired
    UserQueryRepository userQueryRepository;


    @Test
    public void query() {
        // given
        User user = userQueryRepository.findByUserId("lim222").get();
        // when
//
////        SsbPlayListSettings ssbPlayListSettings = plySettingRepository.findOneByTrackId(1L, "6cbf25eb98ebc6a78436", user)
//            .get();
//        ssbPlayListSettings.getPlayListTracks().stream().forEach(track -> System.out.println(
//            "track.getId() = " + track.getId()));
//
//        boolean contains = ssbPlayListSettings.getPlayListTracks().contains(1L);
//        System.out.println("contains = " + contains);
//
//        System.out.println("ssbPlayListSettings.getPlayListTracks().size() = " + ssbPlayListSettings.getPlayListTracks().size());
//
        // then

    }


}
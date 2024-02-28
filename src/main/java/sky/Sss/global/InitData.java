package sky.Sss.global;


import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sky.Sss.domain.track.dto.track.TrackInfoSaveReqDto;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.repository.track.TrackQueryRepository;
import sky.Sss.domain.track.repository.track.TrackRepository;
import sky.Sss.domain.user.dto.join.UserJoinPostDto;
import sky.Sss.domain.user.service.join.UserJoinService;

@Component
@RequiredArgsConstructor
public class InitData {

    private final InitUserService initUserService;

    @PostConstruct
    public void init() {
        initUserService.init();


    }

    @Component
    @RequiredArgsConstructor
    static class InitUserService {
        private final UserJoinService userJoinService;
        private final TrackQueryRepository trackQueryRepository;
        private final TrackRepository trackRepository;
        @PersistenceContext
        private EntityManager em;

        /**
         * 0dksmf071
         * 0dlagksmf2
         */
        public void init() {

            TrackInfoSaveReqDto trackInfoSaveReqDto = new TrackInfoSaveReqDto();

            for (int i = 0; i < 2; i++) {
                UserJoinPostDto userJoinDto = new UserJoinPostDto();
                userJoinDto.setEmail(i + "2512@daum.net");
                userJoinDto.setUserName(i + "유입니다2");
                userJoinDto.setUserId(i + "221325");
                userJoinDto.setPassword(i + "221325");
                userJoinDto.setInfoAgreement(true);
                userJoinDto.setSbbAgreement(true);
                userJoinService.join(userJoinDto);
            }

            UserJoinPostDto userJoinDto = new UserJoinPostDto();
            userJoinDto.setEmail("2221325@naver.com");
            userJoinDto.setUserName("임하늘");
            userJoinDto.setUserId("lim222");
            userJoinDto.setPassword("dlagksmf2");
            userJoinDto.setInfoAgreement(true);
            userJoinDto.setSbbAgreement(true);
            userJoinService.join(userJoinDto);
        }
    }


}

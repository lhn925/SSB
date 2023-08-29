package sky.board.global;


import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import sky.board.domain.user.dto.join.UserJoinAgreeDto;
import sky.board.domain.user.dto.join.UserJoinPostDto;
import sky.board.domain.user.service.join.UserJoinService;

@Component
@Profile("local")
@RequiredArgsConstructor
public class InitUser {

    private final InitUserService initUserService;

    @PostConstruct
    public void init() {
        initUserService.init();

    }

    @Component
    @RequiredArgsConstructor
    static class InitUserService {
        private final UserJoinService userJoinService;
        @PersistenceContext
        private EntityManager em;

        /**
         * 0dksmf071
         * 0dlagksmf2
         */
        public void init() {
            for (int i = 0; i < 2; i++) {
                UserJoinPostDto userJoinDto = new UserJoinPostDto();
                userJoinDto.setEmail(i + "2512@daum.net");
                userJoinDto.setUserName(i + "유입니다2");
                userJoinDto.setUserId(i + "221325");
                userJoinDto.setPassword(i + "221325");
                userJoinService.join(userJoinDto, UserJoinAgreeDto.createUserJoinAgree());
            }

            UserJoinPostDto userJoinDto = new UserJoinPostDto();
            userJoinDto.setEmail("2221325@naver.com");
            userJoinDto.setUserName("임하늘");
            userJoinDto.setUserId("lim222");
            userJoinDto.setPassword("dlagksmf2");
            userJoinService.join(userJoinDto, UserJoinAgreeDto.createUserJoinAgree());


        }
    }
}

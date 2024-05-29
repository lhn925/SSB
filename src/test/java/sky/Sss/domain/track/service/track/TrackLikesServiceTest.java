package sky.Sss.domain.track.service.track;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.SsbTrackLikes;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.global.redis.service.RedisQueryService;


@SpringBootTest
class TrackLikesServiceTest {


    @Autowired
    UserQueryService userQueryService;
    @Autowired
    TrackQueryService trackQueryService;
    @Autowired
    TrackLikesService trackLikesService;

    @Autowired
    RedisQueryService redisQueryService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ObjectMapper objectMapper;




    @Test
    public void like() {

    // given
        User user = userQueryService.findOne("lim2226");


        // when

    // then

    }

    public static class UserToken  {
        private String token;
        private String best;

        public UserToken() {
        }


        public UserToken(String token, String best) {
            this.token = token;
            this.best = best;
        }

        public String getToken() {
            return token;
        }

        public String getBest() {
            return best;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public void setBest(String best) {
            this.best = best;
        }

        // toString 메소드
        @Override
        public String toString() {
            return "UserToken{" +
                "token='" + token + '\'' +
                ", best='" + best + '\'' +
                '}';
        }
    }
    @Test
    public void likesTest() {
//        // given
        User user = userQueryService.findOne("0221325");
        SsbTrack ssbTrack = trackQueryService.findById(1L, Status.ON);
//        // when
//        SsbTrackLikes ssbTrackLikes = SsbTrackLikes.create(user, ssbTrack);
//        // then
//        trackLikesService.addLikes(ssbTrackLikes);
    }
    @Test
    public void findLikeTest() {
        User user = userQueryService.findOne("0221325");
        SsbTrack ssbTrack = trackQueryService.findById(1L, Status.ON);

//        boolean isLikes = trackLikesService.existsLikes(ssbTrack, user);
//        Assertions.assertTrue(isLikes);
    }



    @Test
    public void totalCountTest() {
    // given
        SsbTrack ssbTrack = trackQueryService.findById(1L, Status.ON);
//    // then
//        int totalCount = trackLikesService.getTotalCount(ssbTrack);
//
//        System.out.println("totalCount = " + totalCount);
    }



}
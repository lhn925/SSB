package sky.Sss.domain.track.service.track;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.redis.core.RedisTemplate;
import sky.Sss.domain.track.dto.common.like.LikeSimpleInfoDto;
import sky.Sss.domain.track.dto.common.like.LikedRedisDto;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.SsbTrackLikes;
import sky.Sss.domain.track.repository.track.TrackLikesRepository;
import sky.Sss.domain.user.dto.UserSimpleInfoDto;
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
    TrackLikesRepository trackLikesRepository;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ObjectMapper objectMapper;



    
    @Test
    public void getUserList() {
        List<User> userList = trackLikesService.getUserList("0aac203d6b51f3840d40");

        for (User user : userList) {
            System.out.println("user.getUserId() = " + user.getUserId());

        }
    }

    @Test
    public void getLikeListByTokens() {

        Set<String> keys = new HashSet<>();

        keys.add("0aac203d6b51f3840d40");
        keys.add("22d182fe5d2d9b09667b");
        keys.add("ebb90c922de3f3082dc8");

        List<SsbTrackLikes> ssbTrackLikes = trackLikesRepository.getLikeListByTokens(keys);

    }
    @Test
    public void getLikedRedisDtoList() {

        User user = userQueryService.findOne("lim2226");

        List<LikedRedisDto> dtoList = trackLikesService.getLikedRedisDtoList(user, Sort.by(Order.desc("id")));

        for (LikedRedisDto likedRedisDto : dtoList) {

            System.out.println("likedRedisDto = " + likedRedisDto);
        }
    }
    @Test
    public void getLikeSimpleListByTokens() {

        Set<String> keys = new HashSet<>();

        keys.add("0aac203d6b51f3840d40");
        keys.add("22d182fe5d2d9b09667b");
        keys.add("ebb90c922de3f3082dc8");

        List<LikeSimpleInfoDto> likeSimpleListByTokens = trackLikesRepository.getLikeSimpleListByTokens(keys);

        Map<String, List<UserSimpleInfoDto>> findMap = likeSimpleListByTokens.stream()
            .collect(Collectors.groupingBy(LikeSimpleInfoDto::getToken,
                Collectors.mapping(LikeSimpleInfoDto::getUserSimpleInfoDto, Collectors.toList())));

        for (String findKey : findMap.keySet()) {
            System.out.println("s = " + findKey);
        }
    }

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
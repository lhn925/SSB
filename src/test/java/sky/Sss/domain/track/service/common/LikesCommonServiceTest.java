package sky.Sss.domain.track.service.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.Sss.domain.user.model.ContentsType;

@SpringBootTest
class LikesCommonServiceTest {


    @Autowired
    LikesCommonService likesCommonService;


    @Test
    public void getCountList() {
        // given
        List<String> keys = new ArrayList<>();
        keys.add("0aac203d6b51f3840d40");
//        keys.add("22d182fe5d2d9b09667b");
        keys.add("ebb90c922de3f3082dc8");

        // when
        Map<String, Integer> totalCountMap = likesCommonService.getTotalCountMap(keys, ContentsType.TRACK);

        // then

        for (String token : totalCountMap.keySet()) {
            System.out.println("tokens = " + token);
            System.out.println("totalCountMap.get(tokens) = size " + totalCountMap.get(token));
        }

    }

}
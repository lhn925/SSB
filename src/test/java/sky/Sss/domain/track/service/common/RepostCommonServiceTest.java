package sky.Sss.domain.track.service.common;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.Sss.domain.track.dto.common.repost.RepostSimpleInfoDto;
import sky.Sss.domain.user.model.ContentsType;

@SpringBootTest
class RepostCommonServiceTest {


    @Autowired
    RepostCommonService repostCommonService;


    @Test
    public void getTotalCount() {

        String token = "ebb90c922de3f3082dc8";

        int totalCount = repostCommonService.getTotalCount(token,
            ContentsType.TRACK);

        System.out.println("totalCount = " + totalCount);
    }


    @Test
    public void getTotalCountList() {

        // given
        List<String> keys = new ArrayList<>();

        keys.add("0aac203d6b51f3840d40");
        keys.add("22d182fe5d2d9b09667b");
        keys.add("ebb90c922de3f3082dc8");

        // when
        Map<String, Integer> totalCountMap = repostCommonService.getTotalCountMap(keys,
            ContentsType.TRACK);
        // then
        for (String key : totalCountMap.keySet()) {

            System.out.println("key = " + key);
            System.out.println("totalCountMap = " + totalCountMap.get(key));

        }

    }


}
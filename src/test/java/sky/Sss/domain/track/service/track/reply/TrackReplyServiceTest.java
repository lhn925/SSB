package sky.Sss.domain.track.service.track.reply;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.Sss.domain.track.dto.track.reply.TrackRedisReplyDto;
import sky.Sss.domain.track.entity.track.reply.SsbTrackReply;
import sky.Sss.domain.track.repository.track.reply.TrackReplyRepository;
import sky.Sss.domain.track.service.common.ReplyCommonService;
import sky.Sss.domain.user.model.ContentsType;


@SpringBootTest
class TrackReplyServiceTest {


    @Autowired
    TrackReplyService trackReplyService;

    @Autowired
    TrackReplyRepository trackReplyRepository;

    @Autowired
    ReplyCommonService replyCommonService;

    @Test
    public void totalCount() {
//
//        String trackToken = "ebb90c922de3f3082dc8";
//
//        int totalCount = trackReplyService.getTotalCount(trackToken);
//
//        System.out.println("totalCount = " + totalCount);

    }


    @Test
    public void getRepliesByTokens() {
        Set<String> keys = new HashSet<>();

        keys.add("0aac203d6b51f3840d40");
        keys.add("22d182fe5d2d9b09667b");
        keys.add("ebb90c922de3f3082dc8");

        List<TrackRedisReplyDto> repliesByTokens = trackReplyRepository.getRepliesByTokens(keys);
        for (TrackRedisReplyDto repliesByToken : repliesByTokens) {
            System.out.println("repliesByToken.getId() = " + repliesByToken.getId());


            System.out.println("repliesByToken.getContents() = " + repliesByToken.getContents());
        }
    }


    @Test
    public void getTotalCountList() {
        List<String> keys = new ArrayList<>();

        keys.add("0aac203d6b51f3840d40");
        keys.add("22d182fe5d2d9b09667b");
        keys.add("ebb90c922de3f3082dc8");

        Map<String, Integer> totalCountList = replyCommonService.getTotalCountMap(keys, ContentsType.TRACK);

        for (String targetToken : totalCountList.keySet()) {
            Integer count = totalCountList.get(targetToken);
            System.out.println("targetToken = " + targetToken);
            System.out.println("count = " + count);
        }
    }

    @Test
    public void getReplyCacheFromOrDbByToken () {
        String trackToken = "ebb90c922de3f3082dc8";

        List<SsbTrackReply> trackReplies = trackReplyService.getReplyCacheFromOrDbByToken(trackToken);

        System.out.println("trackReplies.size() = " + trackReplies.size());



    }





}
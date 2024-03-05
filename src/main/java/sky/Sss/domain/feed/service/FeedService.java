package sky.Sss.domain.feed.service;


import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.feed.entity.SsbFeed;
import sky.Sss.domain.feed.model.FeedType;
import sky.Sss.domain.feed.repository.FeedRepository;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.ContentsType;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {

    private final FeedRepository feedRepository;


    @Transactional
    public void addFeed(SsbFeed ssbFeed) {
        feedRepository.save(ssbFeed);
    }


    @Transactional
    public void addFeedList(List<SsbFeed> ssbFeedList) {
        feedRepository.saveAll(ssbFeedList);
    }

    public SsbFeed findOne(User user, long contentsId, ContentsType contentsType) {
        return feedRepository.findOne(user, contentsId, contentsType).orElseThrow(IllegalArgumentException::new);
    }

    public List<SsbFeed> findAllJoinRepost (long contentsId,ContentsType repostType,ContentsType feedType) {
        return feedRepository.findAllJoinRepost(contentsId, repostType, feedType);
    }

    @Transactional
    public void updateReleaseDateTime(User user, long contentsId, ContentsType contentsType, LocalDateTime localDateTime) {
        SsbFeed ssbFeed = findOne(user, contentsId, contentsType);
        SsbFeed.updateReleaseDateTime(ssbFeed,localDateTime);
    }

    @Transactional
    public void deleteFeed(User user, long contentsId, ContentsType contentsType) {
        try {
            SsbFeed ssbFeed = findOne(user, contentsId, contentsType);
            feedRepository.delete(ssbFeed);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

}

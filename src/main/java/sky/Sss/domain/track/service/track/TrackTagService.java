package sky.Sss.domain.track.service.track;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.playList.SsbPlayListTagLink;
import sky.Sss.domain.track.entity.track.SsbTrackTagLink;
import sky.Sss.domain.track.entity.track.SsbTrackTags;
import sky.Sss.domain.track.repository.playList.PlayListTagLinkRepository;
import sky.Sss.domain.track.repository.track.TrackTagLinkRepository;
import sky.Sss.domain.track.repository.track.TrackTagRepository;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TrackTagService {

    private final TrackTagRepository trackTagRepository;
    private final TrackTagLinkRepository trackTagLinkRepository;
    private final PlayListTagLinkRepository playListTagLinkRepository;


    //    Cache miss 로 인한 데이터 실시간성 보완을 위해 cachePut 사용
    @Transactional
    @CachePut(value = "tags", key = "#tag", cacheManager = "contentCacheManager")
    public SsbTrackTags getTagsByStr(String tag) {
        Optional<SsbTrackTags> byTag = trackTagRepository.findByTag(tag);
        return byTag.orElse(null);
    }

    public List<SsbTrackTags> addTags(List<SsbTrackTags> tags) {
        List<SsbTrackTags> ssbTrackTags = trackTagRepository.saveAll(tags);
        return ssbTrackTags;
    }

    // 개별 trackLink Batch 삭제
    @Transactional
    public void deleteTagLinksInBatch(List<SsbTrackTagLink> tagLink) {
        if (tagLink != null && !tagLink.isEmpty()) {
            trackTagLinkRepository.deleteAllInBatch(tagLink);
        }
    }

    // playList trackLink Batch 삭제
    @Transactional
    public void delPlyTagLinksInBatch(List<SsbPlayListTagLink> tagLink) {
        if (tagLink != null && !tagLink.isEmpty()) {
            playListTagLinkRepository.deleteAllInBatch(tagLink);
        }
    }


}

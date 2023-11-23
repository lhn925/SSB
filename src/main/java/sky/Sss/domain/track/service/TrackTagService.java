package sky.Sss.domain.track.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.track.SsbTrackTags;
import sky.Sss.domain.track.repository.TrackTagRepository;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TrackTagService {

    private final TrackTagRepository trackTagRepository;

    @Transactional
//    Cache miss 로 인한 데이터 실시간성 보완을 위해 cachePut 사용
    @CachePut(value = "tags", key = "#tag", cacheManager = "contentCacheManager")
    public SsbTrackTags getTags(String tag) {
        Optional<SsbTrackTags> byTag = trackTagRepository.findByTag(tag);
        SsbTrackTags tags = null;
        if (byTag.isEmpty()) {
            tags = trackTagRepository.save(SsbTrackTags.createSsbTrackTag(tag));
        }
        return tags == null ? byTag.orElse(null) : tags;
    }
}

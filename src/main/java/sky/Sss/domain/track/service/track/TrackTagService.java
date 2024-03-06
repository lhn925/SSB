package sky.Sss.domain.track.service.track;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.tag.TrackTagsDto;
import sky.Sss.domain.track.entity.track.SsbTrackTags;
import sky.Sss.domain.track.repository.track.TrackTagRepositoryImpl;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TrackTagService {

    private final TrackTagRepositoryImpl trackTagRepositoryImpl;

    //    Cache miss 로 인한 데이터 실시간성 보완을 위해 cachePut 사용
    @Transactional
    @CachePut(value = "tags", key = "#tag", cacheManager = "contentCacheManager")
    public SsbTrackTags getTagsByStr(String tag) {
        Optional<SsbTrackTags> byTag = trackTagRepositoryImpl.findByTag(tag);
        return byTag.orElse(null);
    }

    public List<SsbTrackTags> getTagsList(Set<String> tagList) {
        return trackTagRepositoryImpl.findAllByTagIn(tagList);
    }

    @Transactional
    public void addTags(Set<String> tags) {
        LocalDateTime now = LocalDateTime.now();
        List<SsbTrackTags> ssbTrackTags = tags.stream().map(SsbTrackTags::createSsbTrackTag).toList();
        trackTagRepositoryImpl.saveAll(ssbTrackTags,now);
    }

    // DB 태그 검색 후 없으면 추가
    @Transactional
    public List<SsbTrackTags> getSsbTrackTags(List<TrackTagsDto> tagList) {
        if (tagList != null && tagList.size() > 0) {
            Set<String> findTagsSet = tagList.stream().map(TrackTagsDto::getTag).collect(Collectors.toSet());
            List<SsbTrackTags> ssbTrackTags = getTagsList(findTagsSet);
            // String
            List<String> ssbTagStrList = ssbTrackTags.stream().map(SsbTrackTags::getTag).toList();

            // DB에 없어서 추가 해야하는 Tags
            Set<String> addTags = findTagsSet.stream().filter(find -> !ssbTagStrList.contains(find))
                .collect(Collectors.toSet());
            if (!addTags.isEmpty()) {
                // save
                addTags(addTags);
                // 다시 검색 후 저장
                ssbTrackTags.addAll(getTagsList(addTags));
            }
            return ssbTrackTags;
        }
        return null;
    }


}

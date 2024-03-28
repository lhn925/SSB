package sky.Sss.domain.track.service.track;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.tag.TrackTagsDto;
import sky.Sss.domain.track.entity.track.SsbTrackTags;
import sky.Sss.domain.track.repository.track.TrackTagRepositoryImpl;
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisQueryService;
import sky.Sss.global.redis.service.RedisTagService;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TrackTagService {

    private final TrackTagRepositoryImpl trackTagRepositoryImpl;
    private final RedisTagService redisTagService;

    //    Cache miss 로 인한 데이터 실시간성 보완을 위해 cachePut 사용
    @Transactional
    @CachePut(value = "tags", key = "#tag", cacheManager = "contentCacheManager")
    public SsbTrackTags getTagsByStr(String tag) {
        Optional<SsbTrackTags> byTag = trackTagRepositoryImpl.findByTag(tag);
        return byTag.orElse(null);
    }


    public List<SsbTrackTags> getTagsList(Set<String> tagList) {

        List<SsbTrackTags> ssbTrackTagsList = new ArrayList<>();

        List<String> cachingList = new ArrayList<>();

        tagList.forEach(tag -> {
            // 만약 레디스에 tag가 있는 경우
            Map<String, Long> tagMap = redisTagService.searchTagsMap(tag);
            // 값이 있고 아이디값이 0이 아닌 경우
            // tagList 에서 제거 후
            if (!tagMap.isEmpty() && tagMap.get(tag) != 0) {
                cachingList.add(tag); // 검색하지 않을 Tag 들은 cachingList 에
                Long id = tagMap.get(tag);
                SsbTrackTags ssbTrackTag = SsbTrackTags.createSsbTrackTag(tag);
                ssbTrackTag.updateId(id);
                ssbTrackTagsList.add(ssbTrackTag);
                // list 에 추가
            }
        });

        // 이미 레디스에 있는 tag 삭제
        cachingList.forEach(tagList::remove);

        // tagList 가 남아있으면 검색 후
        // redis 에 태그 값과 아이디 값 저장
        if (!tagList.isEmpty()) {
            List<SsbTrackTags> searchTagList = trackTagRepositoryImpl.findAllByTagIn(tagList);
            searchTagList.forEach((searchTag) -> {
                ssbTrackTagsList.add(searchTag);
                redisTagService.addRedisTag(searchTag.getTag(), searchTag.getId());
            });
        }
        return ssbTrackTagsList;
    }

    @Transactional
    public void addTags(Set<String> tags) {
        LocalDateTime now = LocalDateTime.now();
        List<SsbTrackTags> ssbTrackTags = tags.stream().map(SsbTrackTags::createSsbTrackTag).toList();
        trackTagRepositoryImpl.saveAll(ssbTrackTags, now);
        // cache 추가
        ssbTrackTags.forEach((tag) -> {
                log.info("tag.getId() = {}", tag.getId());
                redisTagService.addRedisTag(tag.getTag(), 0);}
        );
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

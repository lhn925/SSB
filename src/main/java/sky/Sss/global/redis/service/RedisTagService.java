package sky.Sss.global.redis.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import sky.Sss.domain.track.dto.tag.TrackTagsDto;
import sky.Sss.global.redis.dto.RedisKeyDto;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisTagService {


    private final RedisTemplate<String, String> redisTemplate;


    // 태그 저장
    // 태그 아이디 값과 함께 저장
    public void addRedisTag(String tags,long id) {
        redisTemplate.opsForZSet().add(RedisKeyDto.REDIS_TAGS_KEY,tags,id);
    }
    // 특정 단어로 시작되는 모든 태그를
    // id 와 함께 맵 형태로 반환
    // 사용자 입력에 기반한 태그 검색
    public List<TrackTagsDto> searchLikeTagsDto(String prefix) {
        // 사용자 입력에 맞는 태그 범위를 지정
//        String searchMin =  prefix;
        String searchMax =  prefix + "\uffff";

        List<TrackTagsDto> tagsDtoList = new ArrayList<>();
        Map<String, Long> tagsLongMap = getTagsLongMap(prefix, searchMax);

        for (String key : tagsLongMap.keySet()) {
            Long id = tagsLongMap.get(key);
            tagsDtoList.add(new TrackTagsDto(id, key));
        }

        return tagsDtoList;
    }
    public Map<String,Long> searchTagsMap(String prefix) {
        // 사용자 입력에 맞는 태그 범위를 지정
        return getTagsLongMap(prefix, prefix);
    }


    public TrackTagsDto searchTagsDto(String prefix) {
        // 사용자 입력에 맞는 태그 범위를 지정
//        String searchMin =  prefix;
        Map<String, Long> tagsLongMap = getTagsLongMap(prefix, prefix);

        for (String key : tagsLongMap.keySet()) {
            Long id = tagsLongMap.get(key);
            return new TrackTagsDto(id, key);
        }
        return new TrackTagsDto(0L, prefix);
    }




    // 특정 단어의 태그를
    // id 와 함께 맵 형태로 반환
    // 사용자 입력에 기반한 태그 검색
    private Map<String, Long> getTagsLongMap(String prefix, String searchMax) {
        // 결과 제한 설정 (예: 최대 10개 결과)
        Limit limit = Limit.limit().count(5);
        // Range 객체 생성
        Range<String> range = Range.closed(prefix, searchMax);
        Set<String> tagSet = redisTemplate.opsForZSet().rangeByLex(RedisKeyDto.REDIS_TAGS_KEY, range, limit);

        Map<String, Long> tagScores = new HashMap<>();
        Objects.requireNonNull(tagSet).forEach(tag -> {
            Double score = Optional.ofNullable(redisTemplate.opsForZSet().score(RedisKeyDto.REDIS_TAGS_KEY, tag)).orElse(0.0);
            tagScores.put(tag, score.longValue());
        });
        return tagScores;
    }

    // 특정 단어로 시작되는 모든 태그 반환
    // 사용자 입력에 기반한 태그 검색
    public Set<String> searchTags(String prefix) {

        // 사용자 입력에 맞는 태그 범위를 지정
        String searchMax =  prefix + "\uffff";
        // 결과 제한 설정 (예: 최대 10개 결과)
        Limit limit = Limit.limit().count(5);
        // Range 객체 생성
        Range<String> range = Range.closed(prefix, searchMax);

        return redisTemplate.opsForZSet().rangeByLex(RedisKeyDto.REDIS_TAGS_KEY, range, limit);
    }


}

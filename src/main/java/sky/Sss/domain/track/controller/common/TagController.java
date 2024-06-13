package sky.Sss.domain.track.controller.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.track.dto.tag.rep.TrackTagsDto;
import sky.Sss.domain.track.entity.track.SsbTrackTags;
import sky.Sss.domain.track.service.track.TrackTagService;
import sky.Sss.global.redis.service.RedisTagService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/tracks/tags")
public class TagController {


    private final RedisTagService redisTagService;
    private final TrackTagService trackTagService;

    /**
     * 코멘트 수정 및 등록
     */
    @GetMapping("/search/{tags}")
    public ResponseEntity<?> getSearchTags(@PathVariable String tags) {

        List<TrackTagsDto> tagsDtoList = new ArrayList<>();
        TrackTagsDto trackTagsDto = redisTagService.searchTagsDto(tags);


        // 태그가 없을 경우 추가
        tagsDtoList.add(trackTagsDto);

        // 추가 후 관련 Tag 검색
        // 위에서 검색한 Tag 태그 삭제 후 list add
        tagsDtoList.addAll(redisTagService.searchLikeTagsDto(tags).stream()
            .filter(data -> !data.getTag().equals(trackTagsDto.getTag()))
            .toList());
        return ResponseEntity.ok(tagsDtoList);
    }


    /**
     * 코멘트 수정 및 등록
     */
    @PostMapping("/{tags}")
    public ResponseEntity<?> addTags(@PathVariable String tags) {
        Set<String> hashSet = new HashSet<>();

        hashSet.add(tags);

        trackTagService.addTags(hashSet);

        List<SsbTrackTags> tagsList = trackTagService.getTagsList(hashSet);

        return ResponseEntity.ok().build();
    }


}

package sky.Sss.domain.track.service.common;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.playList.SsbPlayListTagLink;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.SsbTrackTagLink;
import sky.Sss.domain.track.entity.track.SsbTrackTags;
import sky.Sss.domain.track.repository.playList.PlayListTagLinkRepository;
import sky.Sss.domain.track.repository.playList.PlyTagLinkRepositoryImpl;
import sky.Sss.domain.track.repository.track.TrackTagLinkRepository;
import sky.Sss.domain.track.repository.track.TrackTagLinkRepositoryImpl;
import sky.Sss.global.redis.service.RedisQueryService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagLinkCommonService {

    private final TrackTagLinkRepository trackTagLinkRepository;
    private final TrackTagLinkRepositoryImpl trackTagLinkRepositoryImpl;
    private final PlayListTagLinkRepository playListTagLinkRepository;
    private final PlyTagLinkRepositoryImpl plyTagLinkRepositoryImpl;
    private final RedisQueryService redisQueryService;

    @Transactional
    public void addTrackTagLinks(List<SsbTrackTagLink> ssbTrackTagLinkList) {
        if (ssbTrackTagLinkList.isEmpty()) {
            return;
        }
        trackTagLinkRepositoryImpl.saveAll(ssbTrackTagLinkList, LocalDateTime.now());
    }

    // 태그 등록
    private List<SsbTrackTagLink> getTrackTagLinks(List<SsbTrackTags> tags, SsbTrack ssbTrack) {
        if (!tags.isEmpty()) {
            return tags.stream().map(tag -> SsbTrackTagLink.createSsbTrackTagLink(
                ssbTrack, tag)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }


    @Transactional
    public void addPlyTagLinks(List<SsbPlayListTagLink> ssbPlayListTagLinks) {
        plyTagLinkRepositoryImpl.saveAll(ssbPlayListTagLinks, LocalDateTime.now());
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

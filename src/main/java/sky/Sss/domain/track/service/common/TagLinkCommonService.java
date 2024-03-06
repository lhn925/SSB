package sky.Sss.domain.track.service.common;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.playList.SsbPlayListTagLink;
import sky.Sss.domain.track.entity.track.SsbTrackTagLink;
import sky.Sss.domain.track.repository.playList.PlayListTagLinkRepository;
import sky.Sss.domain.track.repository.playList.PlyTagLinkRepositoryImpl;
import sky.Sss.domain.track.repository.track.TrackTagLinkRepository;
import sky.Sss.domain.track.repository.track.TrackTagLinkRepositoryImpl;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagLinkCommonService {

    private final TrackTagLinkRepository trackTagLinkRepository;
    private final TrackTagLinkRepositoryImpl trackTagLinkRepositoryImpl;
    private final PlayListTagLinkRepository playListTagLinkRepository;
    private final PlyTagLinkRepositoryImpl plyTagLinkRepositoryImpl;

    @Transactional
    public void addTrackTagLinks(List<SsbTrackTagLink> ssbTrackTagLinkList) {
        trackTagLinkRepositoryImpl.saveAll(ssbTrackTagLinkList, LocalDateTime.now());
    }

    @Transactional
    public void addPlyTagLinks (List<SsbPlayListTagLink> ssbPlayListTagLinks) {
        plyTagLinkRepositoryImpl.saveAll(ssbPlayListTagLinks,LocalDateTime.now());
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

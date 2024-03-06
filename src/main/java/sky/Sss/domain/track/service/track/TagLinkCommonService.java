package sky.Sss.domain.track.service.track;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.playList.SsbPlayListTagLink;
import sky.Sss.domain.track.entity.track.SsbTrackTagLink;
import sky.Sss.domain.track.repository.playList.PlayListTagLinkRepository;
import sky.Sss.domain.track.repository.track.TrackTagLinkRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagLinkCommonService {

    private final TrackTagLinkRepository trackTagLinkRepository;
    private final PlayListTagLinkRepository playListTagLinkRepository;


    @Transactional
    public void addTrackTagLinks(List<SsbTrackTagLink> ssbTrackTagLinkList) {
        trackTagLinkRepository.saveAll(ssbTrackTagLinkList);
    }

    @Transactional
    public void addPlyTagLinks (List<SsbPlayListTagLink> ssbPlayListTagLinks) {
        playListTagLinkRepository.saveAll(ssbPlayListTagLinks);
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

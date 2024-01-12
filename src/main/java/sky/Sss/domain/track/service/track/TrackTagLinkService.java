package sky.Sss.domain.track.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.track.SsbTrackTagLink;
import sky.Sss.domain.track.repository.TrackTagLinkRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrackTagLinkService {

    private final TrackTagLinkRepository trackTagLinkRepository;


    @Transactional
    public void saveAll (List<SsbTrackTagLink> ssbTrackTagLinks) {
        trackTagLinkRepository.saveAll(ssbTrackTagLinks);
    }
}

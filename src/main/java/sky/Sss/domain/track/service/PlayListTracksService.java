package sky.Sss.domain.track.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.playList.SsbPlayListTracks;
import sky.Sss.domain.track.repository.PlayListTracksRepository;
import sky.Sss.domain.user.service.UserQueryService;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class PlayListTracksService {
    private final PlayListTracksRepository playListTracksRepository;
    private final UserQueryService userQueryService;

    @Transactional
    public void deleteTracksInBatch(List<SsbPlayListTracks> tracksList) {
        if (!tracksList.isEmpty()) {
            playListTracksRepository.deleteAllInBatch(tracksList);
        }
    }

    @Transactional
    public void deleteBySettingsId (Long settingsId){
        playListTracksRepository.deleteBySettingsId(settingsId);
    }

}

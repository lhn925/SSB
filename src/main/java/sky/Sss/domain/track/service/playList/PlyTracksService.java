package sky.Sss.domain.track.service.playList;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.playList.SsbPlayListTracks;
import sky.Sss.domain.track.repository.playList.PlyTracksRepository;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class PlyTracksService {
    private final PlyTracksRepository plyTracksRepository;

    @Transactional
    public void deleteTracksInBatch(List<SsbPlayListTracks> tracksList) {
        if (!tracksList.isEmpty()) {
            plyTracksRepository.deleteAllInBatch(tracksList);
        }
    }

    @Transactional
    public void addPlayListTracks(List<SsbPlayListTracks> tracksList) {
        plyTracksRepository.saveAll(tracksList);
    }

    @Transactional
    public void deleteBySettingsId (Long settingsId){
        plyTracksRepository.deleteBySettingsId(settingsId);
    }

}

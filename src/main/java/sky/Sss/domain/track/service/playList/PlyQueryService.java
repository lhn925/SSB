package sky.Sss.domain.track.service.playList;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.exception.checked.SsbFileNotFoundException;
import sky.Sss.domain.track.repository.playList.PlyQueryRepository;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Status;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PlyQueryService {

    private final PlyQueryRepository plyQueryRepository;

    public SsbPlayListSettings findOne(Long id, String token, User user, Status isStatus) {
        return plyQueryRepository.findOne(id, user, token, isStatus.getValue())
            .orElseThrow(() -> new SsbFileNotFoundException());
    }

    public SsbPlayListSettings findOne(Long id, String token, Status isStatus) {
        return plyQueryRepository.findOne(id, token, isStatus.getValue())
            .orElseThrow(() -> new SsbFileNotFoundException());
    }
    public SsbPlayListSettings findById(Long id, Status isStatus) {
        return plyQueryRepository.findByIdAndIsStatus(id, isStatus.getValue())
            .orElseThrow(() -> new SsbFileNotFoundException());
    }
    public SsbPlayListSettings findOneJoinUser(Long id, Status isStatus) {
        return plyQueryRepository.findByIdJoinUser(id, isStatus.getValue())
            .orElseThrow(() -> new SsbFileNotFoundException());
    }

}

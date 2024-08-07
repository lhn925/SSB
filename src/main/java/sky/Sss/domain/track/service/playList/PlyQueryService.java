package sky.Sss.domain.track.service.playList;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.common.rep.TargetInfoDto;
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;
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
            .orElseThrow(SsbFileNotFoundException::new);
    }

    public SsbPlayListSettings findOne(Long id, String token, Status isStatus) {
        return plyQueryRepository.findOne(id, token, isStatus.getValue())
            .orElseThrow(SsbFileNotFoundException::new);
    }

    public SsbPlayListSettings findById(Long id, Status isStatus) {
        return plyQueryRepository.findByIdAndIsStatus(id, isStatus.getValue())
            .orElseThrow(SsbFileNotFoundException::new);
    }

    public SsbPlayListSettings findOneJoinUser(Long id, Status isStatus) {
        return plyQueryRepository.findByJoinUser(id, isStatus.getValue())
            .orElseThrow(SsbFileNotFoundException::new);
    }

    public SsbPlayListSettings findOneJoinUser(Long id, String token, Status isStatus) {
        return plyQueryRepository.findByJoinUser(id, token, isStatus.getValue())
            .orElseThrow(SsbFileNotFoundException::new);
    }

    public TargetInfoDto getTargetInfoDto(long id,String token,Status isStatus) {
        return plyQueryRepository.getTargetInfoDto(id, token, isStatus.getValue())
            .orElseThrow(SsbFileNotFoundException::new);
    }

    public TargetInfoDto getTargetInfoDto(long id,Status isStatus) {
        return plyQueryRepository.getTargetInfoDto(id, isStatus.getValue())
            .orElseThrow(SsbFileNotFoundException::new);
    }
}

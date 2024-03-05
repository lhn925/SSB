package sky.Sss.domain.track.service.track;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.common.TargetInfoDto;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.exception.checked.SsbFileNotFoundException;
import sky.Sss.domain.track.repository.track.TrackQueryRepository;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Status;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class TrackQueryService {

    private final TrackQueryRepository trackQueryRepository;

    public SsbTrack findOne(Long id, String token, User user, Status isStatus) {
        return trackQueryRepository.findOne(id, user, token, isStatus.getValue())
            .orElseThrow(SsbFileNotFoundException::new);
    }

    public SsbTrack findOne(Long id, String token, Status isStatus) {
        return trackQueryRepository.findOne(id, token, isStatus.getValue())
            .orElseThrow(SsbFileNotFoundException::new);
    }

    public SsbTrack findById(Long id, Status isStatus) {
        return trackQueryRepository.findByIdAndIsStatus(id, isStatus.getValue())
            .orElseThrow(SsbFileNotFoundException::new);
    }


    public SsbTrack findOneJoinUser(Long id, String token, Status isStatus) {
        return trackQueryRepository.findByIdJoinUser(id, token, isStatus.getValue())
            .orElseThrow(SsbFileNotFoundException::new);
    }

    public SsbTrack findOneJoinUser(Long id, Status isStatus) {
        return trackQueryRepository.findByIdJoinUser(id, isStatus.getValue())
            .orElseThrow(SsbFileNotFoundException::new);
    }

    public Integer getTotalLength(User user) {
        return trackQueryRepository.getTotalTrackLength(user, Status.ON.getValue());
    }

    public TargetInfoDto getTargetInfoDto(long id,String token,Status isStatus) {
        return trackQueryRepository.getTargetInfoDto(id, token, isStatus.getValue())
            .orElseThrow(SsbFileNotFoundException::new);
    }
}

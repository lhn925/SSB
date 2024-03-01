package sky.Sss.domain.track.service.track;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 트랙과 관련된 사용자 활동을 모아 놓은 Service
 */
@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class TrackActionService {

    private final TrackLikesService trackLikesService;


    public int getTotalLikesCount(String token) {
        return trackLikesService.getTotalCount(token);
    }
}

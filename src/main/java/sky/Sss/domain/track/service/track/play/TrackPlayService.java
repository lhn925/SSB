package sky.Sss.domain.track.service.track.play;


import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.track.redis.RedisPlayLogDto;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.log.SsbTrackAllPlayLogs;
import sky.Sss.domain.track.exception.checked.SsbTrackAccessDeniedException;
import sky.Sss.domain.track.service.track.TrackService;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.global.utili.DayTime;

/**
 * track 조회수 증가 및 재생 파일 가져오기
 */

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class TrackPlayService {


    private final TrackService trackService;
    private final UserQueryService userQueryService;
    private final TrackAllPlayLogService trackAllPlayLogService;
    /**
     * 비공개 확인
     * 본인 여부
     * 비회원 여부
     * 한시간 여부
     */
    /**
     * @param id
     *     trackId
     * @return
     */
    @Transactional
    public UrlResource getTrackPlayFile(Long id, String playToken) {
        RedisPlayLogDto playLogs = trackAllPlayLogService.getPlayDto(id, playToken);
        // 요청시간 서버 기준 으로
        long nowMillis = DayTime.localDateTimeToEpochMillis(LocalDateTime.now()).toEpochMilli();
        // 제한시간 지났는지에 대한 여부
        if (nowMillis > playLogs.getExpireTime()) {
            throw new SsbTrackAccessDeniedException("track.error.forbidden", HttpStatus.FORBIDDEN);
        }

        return trackService.getSsbTrackFile(
            playLogs.getTrackToken() + "/" + playLogs.getStoreFileName());
    }
}


package sky.Sss.domain.track.service.track.play;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.track.log.SsbTrackAllPlayLogs;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.exception.checked.SsbPlayIncompleteException;
import sky.Sss.domain.track.exception.checked.SsbTrackAccessDeniedException;
import sky.Sss.domain.track.model.ChartStatus;
import sky.Sss.domain.track.model.PlayStatus;
import sky.Sss.domain.track.repository.track.play.TrackAllPlayLogRepository;
import sky.Sss.domain.user.entity.User;

/**
 * 플레이 조회수 관련 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TrackAllPlayLogService {

    private final TrackAllPlayLogRepository trackAllPlayLogRepository;

    /**
     * "
     * 1분 이상 들으면 플레이 횟수 증가 1분미만은 전부다,
     * 들어야함 정지를 해도 넘겨들어도 1분 이상 채우면 횟수 증가-> 이건 차트에 반영 안됨 단순 조회수
     * 조건을 충족한뒤 일정시간(10초) 이 지난후 집계
     * <p>
     * 한시간에 한번 플레이는 차트에 반영
     * 플레이 도중 정지 및 넘김 시 해당 플레이 집계x -> 차트에 반영 되는 조회수
     * 차트 순위는 08~24 TOP 100은 24시간 조회수 50% + 1시간 이용량 50%
     * 01~07시 : 24시간 이용량 100%
     * 일간: 매일 낮 12시 기준 최근 24시간 이용량을 집계하며, 매일 13시 이후 업데이트된다.
     * 주간: 매주 월요일 낮 12시 기준 최근 7일 간 이용량을 집계하며, 매주 월요일 14시 이후 업데이트된다.
     * 월간: 매월 1일 낮 12시 기준 최근 1개월 간 이용량을 집계하며, 매월 1일 15시 이후 업데이트된다.
     *
     * @return
     */
    @Transactional
    public void add(SsbTrackAllPlayLogs ssbTrackAllPlayLogs) {
        trackAllPlayLogRepository.save(ssbTrackAllPlayLogs);
    }

    public SsbTrackAllPlayLogs findOne(User user, SsbTrack ssbTrack, Long id, String token, ChartStatus chartStatus) {
        return trackAllPlayLogRepository.findOne(id, token, user, ssbTrack, chartStatus)
            .orElseThrow(IllegalArgumentException::new);
    }

    public SsbTrackAllPlayLogs findOne(User user, SsbTrack ssbTrack, Long id, String token, PlayStatus playStatus) {
        return trackAllPlayLogRepository.findOne(id, token, user, ssbTrack, playStatus)
            .orElseThrow(IllegalArgumentException::new);
    }

    public SsbTrackAllPlayLogs findOne(long trackId, String playToken) {
        return trackAllPlayLogRepository.findOne(trackId, playToken)
            .orElseThrow(() -> new SsbTrackAccessDeniedException("track.error.forbidden", HttpStatus.FORBIDDEN));
    }


}


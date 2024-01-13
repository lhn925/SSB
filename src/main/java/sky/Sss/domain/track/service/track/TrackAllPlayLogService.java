package sky.Sss.domain.track.service.track;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.chart.SsbChartIncludedPlays;
import sky.Sss.domain.track.entity.chart.SsbTrackAllPlayLogs;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.model.ChartStatus;
import sky.Sss.domain.track.repository.track.TrackAllPlayLogRepository;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Status;
import sky.Sss.global.base.login.DefaultLocationLog;
import sky.Sss.global.base.login.DeviceDetails;
import sky.Sss.global.locationfinder.dto.UserLocationDto;
import sky.Sss.global.locationfinder.service.LocationFinderService;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TrackAllPlayLogService {

    private final LocationFinderService locationFinderService;
    private final TrackAllPlayLogRepository trackAllPlayLogRepository;
    private final TrackPlayIncludedService trackPlayIncludedService;

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
     * @param ssbTrack
     * @param user
     * @return
     */
    @Transactional
    public SsbTrackAllPlayLogs addPlayLog(User user, SsbTrack ssbTrack, String userAgent) {
        UserLocationDto location = locationFinderService.findLocation();
        // 현재 시간 밀리초 출력
        long nowTimeMillis = System.currentTimeMillis();

        // Instant 객체 생성
        Instant instant = Instant.ofEpochMilli(nowTimeMillis);

        // 시스템의 기본 시간대를 사용하여 nowTimeMillis -> LocalDateTime 으로 변환
        LocalDateTime nowLocalDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        // 유저 지역정보
        DefaultLocationLog defaultLocationLog = DefaultLocationLog.createDefaultLocationLog(Status.ON, location,
            userAgent);
        // 유저 기기 정보
        DeviceDetails deviceDetails = DeviceDetails.create(userAgent);

        SsbTrackAllPlayLogs ssbTrackAllPlayLogs = SsbTrackAllPlayLogs.create(user, ssbTrack, defaultLocationLog,
            deviceDetails, nowTimeMillis, nowLocalDateTime);

        // 현재 시간대에 공식 로그가 있는지 화인
        updateChartStatus(user, ssbTrack, ssbTrackAllPlayLogs);

        trackAllPlayLogRepository.save(ssbTrackAllPlayLogs);
        return ssbTrackAllPlayLogs;
    }

    // 현재 시간대에 공식 로그가 있는지 화인
    private void updateChartStatus(User user, SsbTrack ssbTrack, SsbTrackAllPlayLogs ssbTrackAllPlayLogs) {
        // 현재 시간대에 공식 로그가 있는지 화인
        SsbChartIncludedPlays includedLog = trackPlayIncludedService.findOne(user, ssbTrack,
            ssbTrackAllPlayLogs.getCreatedDateTime());
        // 공식 재생 여부 확인
        // true : 공식재생, false : 공식 재생 x
        ChartStatus chartStatus = ChartStatus.getChartStatus(includedLog == null);
        // 차트에 반영 되는 재생인지 저장
        SsbTrackAllPlayLogs.updateChartStatus(ssbTrackAllPlayLogs, chartStatus);
    }

}


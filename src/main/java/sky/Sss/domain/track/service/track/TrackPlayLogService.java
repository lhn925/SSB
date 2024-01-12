package sky.Sss.domain.track.service.track;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.chart.SsbTrackAllPlayLogs;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.repository.track.TrackAllPlayLogRepository;
import sky.Sss.domain.track.repository.track.TrackLogRepository;
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
public class TrackPlayLogService {

    private final LocationFinderService locationFinderService;
    private final TrackAllPlayLogRepository trackAllPlayLogRepository;

    @Transactional
    public void save(User user, SsbTrack ssbTrack, String userAgent, Boolean success) {
        UserLocationDto location = locationFinderService.findLocation();
        // 현재 시간 밀리초 출력
        long nowTimeMillis = System.currentTimeMillis();

        // Instant 객체 생성
        Instant instant = Instant.ofEpochMilli(nowTimeMillis);

        // 시스템의 기본 시간대를 사용하여 LocalDateTime 으로 변환
        LocalDateTime nowLocalDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        // 유저 지역정보
        DefaultLocationLog defaultLocationLog = DefaultLocationLog.createDefaultLocationLog(Status.ON, location,
            userAgent);
        // 유저 기기 정보
        DeviceDetails deviceDetails = DeviceDetails.create(userAgent);

        SsbTrackAllPlayLogs ssbTrackAllPlayLogs = SsbTrackAllPlayLogs.create(user,ssbTrack,defaultLocationLog,deviceDetails,nowTimeMillis);
        trackAllPlayLogRepository.save(ssbTrackAllPlayLogs);
    }
}

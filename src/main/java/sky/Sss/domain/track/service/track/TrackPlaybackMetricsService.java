package sky.Sss.domain.track.service.track;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.track.TrackInfoReqDto;
import sky.Sss.domain.track.dto.track.TrackPlayRepDto;
import sky.Sss.domain.track.dto.track.log.TrackPlayLogModifyRepDto;
import sky.Sss.domain.track.dto.track.log.TrackChartSaveReqDto;
import sky.Sss.domain.track.dto.track.log.TrackPlayLogModifyReqDto;
import sky.Sss.domain.track.dto.track.log.TrackPlayLogRepDto;
import sky.Sss.domain.track.entity.chart.SsbChartIncludedPlays;
import sky.Sss.domain.track.entity.chart.SsbTrackAllPlayLogs;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.model.ChartStatus;
import sky.Sss.domain.track.model.PlayStatus;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.global.base.login.DefaultLocationLog;
import sky.Sss.global.base.login.DeviceDetails;
import sky.Sss.global.locationfinder.dto.UserLocationDto;
import sky.Sss.global.locationfinder.service.LocationFinderService;


/**
 * 재생 관련 메트릭스, 즉 재생 로그와 차트 데이터를 분석하는 역할을 강조하는 이름입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrackPlaybackMetricsService {

    private final LocationFinderService locationFinderService;
    private final TrackChatIncludedService trackChatIncludedService;
    private final TrackAllPlayLogService trackAllPlayLogService;
    private final TrackQueryService trackQueryService;
    private final UserQueryService userQueryService;

    public SsbChartIncludedPlays getChartIncludedPlay(User user, SsbTrack ssbTrack, LocalDateTime playDateTime) {
        return trackChatIncludedService.findOne(user, ssbTrack, playDateTime);
    }

    /**
     * @param user
     * @param ssbTrack
     * @param id
     * @param token
     * @return
     */
    public SsbTrackAllPlayLogs getSsbTrackAllPlayLogs(User user, SsbTrack ssbTrack, Long id, String token,ChartStatus chartStatus) {
        return trackAllPlayLogService.findOne(user, ssbTrack, id, token, chartStatus);
    }

    /**
     * @param user
     * @param ssbTrack
     * @param id
     * @param token
     * @return
     */
    public SsbTrackAllPlayLogs getSsbTrackAllPlayLogs(User user, SsbTrack ssbTrack, Long id, String token) {
        return trackAllPlayLogService.findOne(user, ssbTrack, id, token);
    }


    @Transactional
    public void createChartIncluded(TrackChartSaveReqDto trackChartSaveReqDto) {
        TrackInfoReqDto trackInfoReqDto = trackChartSaveReqDto.getTrackInfoReqDto();
        User user = userQueryService.findOne();
        SsbTrack ssbTrack = trackQueryService.findOne(trackInfoReqDto.getId(), trackInfoReqDto.getToken(), Status.ON);

        SsbTrackAllPlayLogs ssbTrackAllPlayLogs = getSsbTrackAllPlayLogs(user, ssbTrack, trackChartSaveReqDto.getId(),
            trackChartSaveReqDto.getToken(),ChartStatus.REFLECTED);
        boolean isReflected = true;
        try {
            // 이미 테이블 에 반영되어 있는지
            checkEq(getChartIncludedPlay(user, ssbTrack,
                ssbTrackAllPlayLogs.getCreatedDateTime()) == null);

            // 플레이타임 이 음원 재생 길이와 일치한지
            checkEqPlayTime(trackChartSaveReqDto, ssbTrack);

            // startTime 과 closeTime 의 간격이 음원재생길이와 유사한지
            checkCloseTime(trackChartSaveReqDto, ssbTrack, ssbTrackAllPlayLogs);
        } catch (IllegalArgumentException e) {
            isReflected = false;
        }
        SsbTrackAllPlayLogs.updatePlayStatus(ssbTrackAllPlayLogs,PlayStatus.COMPLETED);
        SsbTrackAllPlayLogs.updateChartStatus(ssbTrackAllPlayLogs,ChartStatus.getChartStatus(isReflected));
        // 모든 충족이 만족되면
        if (isReflected) {
            SsbChartIncludedPlays ssbChartIncludedPlays = SsbChartIncludedPlays.create(ssbTrackAllPlayLogs);
            trackChatIncludedService.save(ssbChartIncludedPlays);
        }
        // 세가지 결과가 전부 다 옳다면
    }


    @Transactional
    public void createAllPlayLog(String userAgent, TrackPlayRepDto trackPlayRepDto, SsbTrack ssbTrack, User user) {
        // 현재시간 생성
        UserLocationDto location = locationFinderService.findLocation();

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

        // 객체 생성
        SsbTrackAllPlayLogs ssbTrackAllPlayLogs = SsbTrackAllPlayLogs.create(user, ssbTrack, defaultLocationLog,
            deviceDetails, nowTimeMillis, nowLocalDateTime);
        Boolean checkHour = checkHour(ssbTrack, nowLocalDateTime);
        // checkHour
        if (checkHour) {
            checkChartStatus(user, ssbTrack, ssbTrackAllPlayLogs);
        } else {
            SsbTrackAllPlayLogs.updateChartStatus(ssbTrackAllPlayLogs, ChartStatus.NOT_REFLECTED);
        }
        // insert
        trackAllPlayLogService.save(ssbTrackAllPlayLogs);
        // 로그 정보 dto 생성
        TrackPlayLogRepDto trackPlayLogRepDto = TrackPlayLogRepDto.create(ssbTrackAllPlayLogs);
        // trackPlayRepDto set
        TrackPlayRepDto.updateTrackPlayLogRepDto(trackPlayRepDto, trackPlayLogRepDto);
    }


    /**
     * playLog 수정 사항
     * @param trackPlayLogModifyReqDto
     * @return
     */
    @Transactional
    public void modifyPlayLog(TrackPlayLogModifyReqDto trackPlayLogModifyReqDto) {
        TrackInfoReqDto trackInfoReqDto = trackPlayLogModifyReqDto.getTrackInfoReqDto();
        User user = userQueryService.findOne();
        SsbTrack ssbTrack = trackQueryService.findOne(trackInfoReqDto.getId(), trackInfoReqDto.getToken(), user, Status.ON);
        SsbTrackAllPlayLogs ssbTrackAllPlayLogs = getSsbTrackAllPlayLogs(user, ssbTrack,
            trackPlayLogModifyReqDto.getId(), trackPlayLogModifyReqDto.getToken());
        if (ssbTrackAllPlayLogs.getPlayStatus().equals(PlayStatus.COMPLETED)) {
            return;
        }

        // REFLECTED 플레이 로그였다면 -> NOT_REFLECTED
        if (ssbTrackAllPlayLogs.getChartStatus().equals(ChartStatus.REFLECTED)) {
            SsbTrackAllPlayLogs.updateChartStatus(ssbTrackAllPlayLogs,ChartStatus.NOT_REFLECTED);
        }

        int playTime = trackPlayLogModifyReqDto.getPlayTime();

        // 최소 플레이 재생 시간(60초 혹은 1분 미만은 60초 전부)을 넘겼거나 같은지
        int minPlayTime = ssbTrackAllPlayLogs.getMinimumPlayTime();

        // 충족 했는지
        boolean isMinTime = playTime >= minPlayTime;

        // startTime 과 closeTime 의 간격이 최소플레이 재생 시간을 넘겼거나 같은지
        boolean isCloseTime = (trackPlayLogModifyReqDto.getCloseTime() - ssbTrackAllPlayLogs.getStartTime() / 1000) >= minPlayTime;
        // 둘다 충족했으면
        if (isCloseTime && isMinTime) {
            SsbTrackAllPlayLogs.updatePlayStatus(ssbTrackAllPlayLogs,PlayStatus.COMPLETED);
        }
        SsbTrackAllPlayLogs.updateCloseTime(ssbTrackAllPlayLogs, trackPlayLogModifyReqDto.getCloseTime());
    }

    // 현재 시간대에 공식 로그가 있는지 화인
    private void checkChartStatus(User user, SsbTrack ssbTrack, SsbTrackAllPlayLogs ssbTrackAllPlayLogs) {
        // 현재 시간대에 공식 로그가 있는지 화인
        SsbChartIncludedPlays includedLog = getChartIncludedPlay(user, ssbTrack,
            ssbTrackAllPlayLogs.getCreatedDateTime());
        // 공식 재생 여부 확인
        // true : 공식재생, false : 공식 재생 x
        boolean isChartLog = includedLog == null;
        ChartStatus chartStatus = ChartStatus.getChartStatus(isChartLog);
        // 차트에 반영 되는 재생인지 저장
        SsbTrackAllPlayLogs.updateChartStatus(ssbTrackAllPlayLogs, chartStatus);
    }

    private boolean checkHour(SsbTrack ssbTrack, LocalDateTime nowLocalDateTime) {
        // 재생이 끝났을 경우의 시간대
        int closeHour = nowLocalDateTime.plusSeconds(ssbTrack.getTrackLength()).getHour();
        // 요청 시간대
        int nowHour = nowLocalDateTime.getHour();

        return nowHour == closeHour;
    }


    private void checkCloseTime(TrackChartSaveReqDto trackChartSaveReqDto, SsbTrack ssbTrack,
        SsbTrackAllPlayLogs ssbTrackAllPlayLogs) {
        boolean isEqCloseTime = (trackChartSaveReqDto.getCloseTime() - ssbTrackAllPlayLogs.getStartTime() / 1000)
            == ssbTrack.getTrackLength();
        checkEq(isEqCloseTime);
    }

    private void checkEq(boolean isInclude) {
        if (!isInclude) {
            throw new IllegalArgumentException();
        }
    }

    private void checkEqPlayTime(TrackChartSaveReqDto trackChartSaveReqDto, SsbTrack ssbTrack) {
        checkEq(trackChartSaveReqDto.getPlayTime() == ssbTrack.getTrackLength());
    }
}

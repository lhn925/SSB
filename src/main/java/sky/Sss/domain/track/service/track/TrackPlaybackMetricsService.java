package sky.Sss.domain.track.service.track;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.track.TrackInfoReqDto;
import sky.Sss.domain.track.dto.track.TrackPlayRepDto;
import sky.Sss.domain.track.dto.track.log.TrackChartSaveReqDto;
import sky.Sss.domain.track.dto.track.log.TrackPlayLogModifyReqDto;
import sky.Sss.domain.track.dto.track.log.TrackPlayLogRepDto;
import sky.Sss.domain.track.entity.chart.SsbChartIncludedPlays;
import sky.Sss.domain.track.entity.chart.SsbTrackAllPlayLogs;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.exception.checked.SsbPlayIncompleteException;
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
    public SsbTrackAllPlayLogs getSsbTrackAllPlayLogs(User user, SsbTrack ssbTrack, Long id, String token,
        ChartStatus chartStatus) {
        return trackAllPlayLogService.findOne(user, ssbTrack, id, token, chartStatus);
    }

    /**
     * @param user
     * @param ssbTrack
     * @param id
     * @param token
     * @return
     */
    public SsbTrackAllPlayLogs getSsbTrackAllPlayLogs(User user, SsbTrack ssbTrack, Long id, String token,PlayStatus playStatus) {
        return trackAllPlayLogService.findOne(user, ssbTrack, id, token,playStatus);
    }


    /**
     * 차트 반영 조회수 생성 (조건 충족되지 않은 경우 일반조회수로 변경)
     *
     * @param trackChartSaveReqDto
     */
    @Transactional
    public void createChartIncluded(TrackChartSaveReqDto trackChartSaveReqDto) {
        TrackInfoReqDto trackInfoReqDto = trackChartSaveReqDto.getTrackInfoReqDto();
        User user = userQueryService.findOne();
        SsbTrack ssbTrack = trackQueryService.findOne(trackInfoReqDto.getId(), trackInfoReqDto.getToken(), Status.ON);
        SsbTrackAllPlayLogs ssbTrackAllPlayLogs = getSsbTrackAllPlayLogs(user, ssbTrack, trackChartSaveReqDto.getId(),
            trackChartSaveReqDto.getToken(), ChartStatus.REFLECTED);

        boolean isReflected = true;

        try {
            // 이미 테이블 에 반영되어 있는지
            boolean isChartCreated =
                getChartIncludedPlay(user, ssbTrack, ssbTrackAllPlayLogs.getCreatedDateTime()) == null;
            checkEq(isChartCreated);

            // 플레이타임 이 Track 음원 재생 길이와 일치한지
            checkEqPlayTime(trackChartSaveReqDto.getPlayTime(), ssbTrack.getTrackLength());
            long userCloseTime = SsbTrackAllPlayLogs.removeMillis(trackChartSaveReqDto.getCloseTime());

            // startTime 과 closeTime 의 간격이 음원재생길이와 유사한지
            checkCloseTime(userCloseTime, ssbTrackAllPlayLogs.getCloseTime());
        }catch (SsbPlayIncompleteException e) {
            isReflected = false;
        }

        SsbTrackAllPlayLogs.updatePlayStatus(ssbTrackAllPlayLogs, trackChartSaveReqDto.getPlayTime());
        SsbTrackAllPlayLogs.updateTotalPlayTime(ssbTrackAllPlayLogs, trackChartSaveReqDto.getPlayTime());
        SsbTrackAllPlayLogs.updateChartStatus(ssbTrackAllPlayLogs, isReflected);

        // 모든 충족이 만족되면
        if (isReflected) {
            saveChartLog(ssbTrackAllPlayLogs);
        }
        // 세가지 결과가 전부 다 옳다면
    }

    public void saveChartLog(SsbTrackAllPlayLogs ssbTrackAllPlayLogs) {
        SsbChartIncludedPlays ssbChartIncludedPlays = SsbChartIncludedPlays.create(ssbTrackAllPlayLogs);
        trackChatIncludedService.save(ssbChartIncludedPlays);
    }


    /**
     * 일반 조회수 생성
     *
     * @param userAgent
     * @param trackPlayRepDto
     * @param ssbTrack
     * @param user
     */
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

        // 예상 closeTime 계산
        LocalDateTime closeDateTime = nowLocalDateTime.plusSeconds(ssbTrack.getTrackLength());
        ZonedDateTime zonedDateTime = closeDateTime.atZone(ZoneId.systemDefault());
        Instant zoneInstant = zonedDateTime.toInstant();

        long exCloseTime = zoneInstant.toEpochMilli();

        boolean checkHour = checkHour(closeDateTime, nowLocalDateTime);

        // 예상 closeTime 저장
        SsbTrackAllPlayLogs.updateCloseTime(ssbTrackAllPlayLogs, exCloseTime);

        // checkHour 로 판단  ChartStatus 저장
        SsbTrackAllPlayLogs.updateChartStatus(ssbTrackAllPlayLogs, checkHour);
        // checkHour True일 경우 Chart 있는지 확인
        if (ssbTrackAllPlayLogs.getChartStatus().equals(ChartStatus.REFLECTED)) {
            checkChartStatus(user, ssbTrack, ssbTrackAllPlayLogs);
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
     *
     * @param trackPlayLogModifyReqDto
     * @return
     */
    @Transactional
    public void modifyPlayLog(TrackPlayLogModifyReqDto trackPlayLogModifyReqDto) throws SsbPlayIncompleteException {
        TrackInfoReqDto trackInfoReqDto = trackPlayLogModifyReqDto.getTrackInfoReqDto();
        User user = userQueryService.findOne();
        SsbTrack ssbTrack = trackQueryService.findOne(trackInfoReqDto.getId(), trackInfoReqDto.getToken(),
            Status.ON);
        SsbTrackAllPlayLogs ssbTrackAllPlayLogs = getSsbTrackAllPlayLogs(user, ssbTrack,
            trackPlayLogModifyReqDto.getId(), trackPlayLogModifyReqDto.getToken(),PlayStatus.INCOMPLETE);
        // REFLECTED 플레이 로그였다면 -> NOT_REFLECTED
        if (ssbTrackAllPlayLogs.getChartStatus().equals(ChartStatus.REFLECTED)) {
            SsbTrackAllPlayLogs.updateChartStatus(ssbTrackAllPlayLogs, false);
        }
        long userCloseTime = SsbTrackAllPlayLogs.removeMillis(trackPlayLogModifyReqDto.getCloseTime());

        // 사용자가 플레이 재생 시간
        int playTime = trackPlayLogModifyReqDto.getPlayTime();

        // 최소 플레이 재생 시간
        int minPlayTime = ssbTrackAllPlayLogs.getMinimumPlayTime();

        // 최소시간을 충족 했는지 (60초 혹은 1분 미만은 60초 전부)
        boolean isMinTime = playTime >= minPlayTime;

        int secondBetween = secondBetween(ssbTrackAllPlayLogs.getStartTime(), userCloseTime);
        // startTime 과 closeTime 의 간격이 최소플레이 재생 시간을 넘겼거나 같은지
        boolean isCloseTime = secondBetween >= minPlayTime;

        /**
         * 정지 했다가 10초 정도
         * 다음버튼 이전버튼을 눌렀을 경우도 10초있다
         */

        SsbTrackAllPlayLogs.updateTotalPlayTime(ssbTrackAllPlayLogs, trackPlayLogModifyReqDto.getPlayTime());
        // 둘다 충족했으면
        // 뒤로감기나 앞으로가기 버튼을 눌렀을 경우
        if (isCloseTime && isMinTime) {
            SsbTrackAllPlayLogs.updatePlayStatus(ssbTrackAllPlayLogs, trackPlayLogModifyReqDto.getPlayTime());
        } else {
            throw new SsbPlayIncompleteException();
        }
    }

    // 현재 시간대에 공식 로그가 있는지 화인
    private void checkChartStatus(User user, SsbTrack ssbTrack, SsbTrackAllPlayLogs ssbTrackAllPlayLogs) {
        // 현재 시간대에 공식 로그가 있는지 화인
        SsbChartIncludedPlays includedLog = getChartIncludedPlay(user, ssbTrack,
            ssbTrackAllPlayLogs.getCreatedDateTime());
        // 공식 재생 여부 확인
        // true : 공식재생, false : 공식 재생 x
        boolean isChartLog = includedLog == null;
        // 차트에 반영 되는 재생인지 저장
        SsbTrackAllPlayLogs.updateChartStatus(ssbTrackAllPlayLogs, isChartLog);
    }

    /**
     * 재생이 끝났을 경우의 예상 시간대를 구해서
     * 비교한뒤 true 혹은 false를 반영
     * 넘어갈경우 차트 반영 x
     */
    private boolean checkHour(LocalDateTime closeDateTime, LocalDateTime nowLocalDateTime) {
        // 재생이 끝났을 경우의 예상 시간대
        int closeHour = closeDateTime.getHour();
        // 요청 시간대
        int nowHour = nowLocalDateTime.getHour();

        return nowHour == closeHour;
    }


    /**
     * 서버에 저장되어 있는 closeTime과
     * 유저가 보낸 closeTime 이 일치한지
     */
    private void checkCloseTime(long userCloseTime, long dbCloseTime) throws SsbPlayIncompleteException {
        // 사용자가 보낸 closeTime
        boolean isEqCloseTime = (userCloseTime == dbCloseTime);
        checkEq(isEqCloseTime);
    }


    private void checkEq(boolean isInclude) throws SsbPlayIncompleteException {
        if (!isInclude) {
            throw new SsbPlayIncompleteException();
        }
    }

    /**
     * 유저가 보낸 playTime 과 서버에 저장되어 있는 TrackLength 길이가 맞는지
     */
    private void checkEqPlayTime(int playTime, int trackLength) throws SsbPlayIncompleteException {
        boolean isPlayTime = playTime == trackLength;
        checkEq(isPlayTime);
    }

    private int secondBetween(long startTime, long closeTime) {
        return (int) ((closeTime - startTime) / 1000);
    }
}

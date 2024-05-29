package sky.Sss.domain.track.service.track.play;


import static sky.Sss.global.utili.DayTime.millisToLocalDateTime;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.track.req.TrackInfoReqDto;
import sky.Sss.domain.track.dto.track.rep.TrackPlayRepDto;
import sky.Sss.domain.track.dto.track.chart.DailyPlaysSearchDto;
import sky.Sss.domain.track.dto.track.chart.TrackTotalPlaysDto;
import sky.Sss.domain.track.dto.track.chart.TrackChartSaveReqDto;
import sky.Sss.domain.track.dto.track.log.TrackPlayLogModifyReqDto;
import sky.Sss.domain.track.dto.track.log.TrackPlayLogRepDto;
import sky.Sss.domain.track.entity.chart.SsbChartDaily;
import sky.Sss.domain.track.entity.chart.SsbChartHourly;
import sky.Sss.domain.track.entity.chart.SsbChartIncludedPlays;
import sky.Sss.domain.track.entity.track.log.SsbTrackAllPlayLogs;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.log.SsbTrackDailyTotalPlays;
import sky.Sss.domain.track.entity.track.log.SsbTrackHourlyTotalPlays;
import sky.Sss.domain.track.exception.checked.SsbPlayIncompleteException;
import sky.Sss.domain.track.model.ChartStatus;
import sky.Sss.domain.track.model.PlayStatus;
import sky.Sss.domain.track.service.chart.TrackChartDailyService;
import sky.Sss.domain.track.service.chart.TrackChartHourlyService;
import sky.Sss.domain.track.service.chart.TrackChatIncludedService;
import sky.Sss.domain.track.service.track.TrackQueryService;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.global.base.login.DefaultLocationLog;
import sky.Sss.global.base.login.DeviceDetails;
import sky.Sss.global.locationfinder.dto.UserLocationDto;
import sky.Sss.global.locationfinder.service.LocationFinderService;
import sky.Sss.global.utili.DayTime;


/**
 * 재생 관련 메트릭스, 즉 재생 로그와 차트 데이터를 분석하고 생성하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrackPlayMetricsService {

    private final LocationFinderService locationFinderService;
    private final TrackChatIncludedService trackChatIncludedService;
    private final TrackAllPlayLogService trackAllPlayLogService;
    private final TrackQueryService trackQueryService;
    private final UserQueryService userQueryService;

    private final TrackHourlyTotalPlaysService trackHourlyTotalPlaysService;
    private final TrackDailyTotalPlaysService trackDailyTotalPlaysService;

    private final TrackChartHourlyService trackChartHourlyService;
    private final TrackChartDailyService trackChartDailyService;

    /**
     * @param user
     * @param ssbTrack
     * @param id
     * @param token
     * @return
     */
    public SsbTrackAllPlayLogs getSsbTrackAllPlayLogs(User user, SsbTrack ssbTrack, String token,
        PlayStatus playStatus) {

        return trackAllPlayLogService.findOne(user, ssbTrack, token, playStatus);
    }

    // include 테이블 총합


    /**
     * 차트 반영 조회수 생성 (조건 충족되지 않은 경우 일반조회수로 변경)
     *
     * @param trackChartSaveReqDto
     */
    @Transactional
    public void addChartIncluded(TrackChartSaveReqDto trackChartSaveReqDto) {
        TrackInfoReqDto trackInfoReqDto = trackChartSaveReqDto.getTrackInfoReqDto();
        User user = userQueryService.findOne();
        SsbTrack ssbTrack = trackQueryService.findById(trackInfoReqDto.getId(), Status.ON);
        SsbTrackAllPlayLogs ssbTrackAllPlayLogs = getSsbTrackAllPlayLogs(user, ssbTrack,
            trackChartSaveReqDto.getToken(), ChartStatus.REFLECTED);

        // 반영여부
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
        } catch (SsbPlayIncompleteException e) {
            isReflected = false;
        }

        PlayStatus playStatus = validPlayTime(ssbTrackAllPlayLogs.getMinimumPlayTime(),
            trackChartSaveReqDto.getPlayTime());
        SsbTrackAllPlayLogs.updatePlayStatus(ssbTrackAllPlayLogs,
            playStatus);

        trackAllPlayLogService.completeLogSaveRedisCache(ssbTrackAllPlayLogs);
        SsbTrackAllPlayLogs.updateTotalPlayTime(ssbTrackAllPlayLogs, trackChartSaveReqDto.getPlayTime());
        SsbTrackAllPlayLogs.updateChartStatus(ssbTrackAllPlayLogs, isReflected);

        // insert into ssb_chart_included_plays (created_date_time, day_time, last_modified_date_time, track_id, log_id, id) values ('2024-02-12 05:40:18.142732', 2024021214, '2024-02-12 05:40:18.142732', 1, 1, 1);
        // 모든 충족이 만족되면
        if (isReflected) {
            SsbChartIncludedPlays ssbChartIncludedPlays = SsbChartIncludedPlays.create(ssbTrackAllPlayLogs);
            trackChatIncludedService.save(ssbChartIncludedPlays);
        }
        // 세가지 결과가 전부 다 옳다면
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
    public void addAllPlayLog(String userAgent, TrackPlayRepDto trackPlayRepDto, SsbTrack ssbTrack, User user) {
        // 현재시간 생성
        UserLocationDto location = locationFinderService.findLocation();

        long nowTimeMillis = System.currentTimeMillis();
        LocalDateTime nowLocalDateTime = millisToLocalDateTime(nowTimeMillis);
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

        long exCloseTime = DayTime.localDateTimeToEpochMillis(closeDateTime).toEpochMilli();

        // 예상 closeTime 저장
        SsbTrackAllPlayLogs.updateCloseTime(ssbTrackAllPlayLogs, exCloseTime);
        // 자신의 트랙이 아닐 경우에만 차트반영
        if (!ssbTrack.getUser().getToken().equals(user.getToken())) {
            boolean checkHour = checkHour(closeDateTime, nowLocalDateTime);
            // checkHour 로 판단  ChartStatus 저장
            SsbTrackAllPlayLogs.updateChartStatus(ssbTrackAllPlayLogs, checkHour);
            // checkHour True일 경우 Chart 있는지 확인
            if (ssbTrackAllPlayLogs.getChartStatus().equals(ChartStatus.REFLECTED)) {
                checkChartStatus(user, ssbTrack, ssbTrackAllPlayLogs);
            }
        } else {
            // 자신의 트랙인 경우에는 무조건 false
            // 차트 반영 안됨
            // 기본조회수는 반영이 됨
            SsbTrackAllPlayLogs.updateChartStatus(ssbTrackAllPlayLogs, false);
        }

        // insert
        trackAllPlayLogService.add(ssbTrackAllPlayLogs);
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
        SsbTrack ssbTrack = trackQueryService.findById(trackInfoReqDto.getId(), Status.ON);
        SsbTrackAllPlayLogs ssbTrackAllPlayLogs = getSsbTrackAllPlayLogs(user, ssbTrack,
            trackPlayLogModifyReqDto.getToken(), PlayStatus.INCOMPLETE);
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
            PlayStatus playStatus = validPlayTime(
                ssbTrackAllPlayLogs.getMinimumPlayTime(), trackPlayLogModifyReqDto.getPlayTime());
            SsbTrackAllPlayLogs.updatePlayStatus(ssbTrackAllPlayLogs, playStatus);
            trackAllPlayLogService.completeLogSaveRedisCache(ssbTrackAllPlayLogs);
        } else {
            throw new SsbPlayIncompleteException();
        }
    }


    // 플레이타임 검증
    public PlayStatus validPlayTime(int minimumPlayTime, int playTime) {

        return PlayStatus.getPlayStatus(minimumPlayTime <= playTime);

    }


    /**
     * track 시간대 별 조회수 데이터 생성
     * createTrackHourlyTotalPlays
     *
     * @param dayTime
     */
    @Transactional
    public void addTrackHourlyTotalPlays(int dayTime) {
        /**
         * 해당 dayTime이 생성 되 었는지 확인 후
         */

        // 한시간동안의 공식 조회수 조회 후
        // SsbTrackHourlyTotalPlays List 생성
        List<SsbTrackHourlyTotalPlays> totalPlays = trackChatIncludedService.hourlyChartFindByDayTime(dayTime).stream()
            .map(SsbTrackHourlyTotalPlays::create
            ).collect(Collectors.toList());
        trackHourlyTotalPlaysService.addAll(totalPlays);
    }

    /**
     * track 일간 총 조회수 생성
     * createTrackHourlyTotalPlays
     *
     * @param startDayTime
     * @param endDayTime
     */
    @Transactional
    public void addTrackDailyTotalPlays(int startDayTime, int endDayTime) {
        List<SsbTrackDailyTotalPlays> totalPlays = trackHourlyTotalPlaysService.getDailyTotalPlayDtoList(
                startDayTime,
                endDayTime).stream().map(dto -> SsbTrackDailyTotalPlays.create(dto, endDayTime))
            .collect(Collectors.toList());
        trackDailyTotalPlaysService.addAll(totalPlays);
    }


    /**
     * 시간대 별 차트 생성
     *
     * @param ranDayTime
     * @param startDayTime
     *     // 최근 24시간 시작 시간대
     * @param endDayTime
     *     // 최근 24시간 마지막 시간대
     */
    @Transactional
    public void addChartHourly(int ranDayTime, int startDayTime, int endDayTime, PageRequest pageRequest) {
        final int NUM = 10;
        // 차트 계산 비율 변수
        final double DIV = 0.2;
        // 최근 24시간 sum
        Map<Long, Long> dailyMap = null;
        if (trackChartHourlyService.checkChart(ranDayTime)) {
            return;
        }

        // 생성 할려는 시간대 종합 조회수 리스트 불러오기
        List<TrackTotalPlaysDto> hourlyPlayList = getHourlyPlaysDtos(ranDayTime, endDayTime,
            pageRequest);

        // 현재 시간대 트랙 플레이 검색
        List<SsbTrack> ssbTracks = hourlyPlayList.stream().map(TrackTotalPlaysDto::getSsbTrack)
            .collect(Collectors.toList());

        // 최근 24시간 조회수 sum
        // dailyMap 생성
        if (!ssbTracks.isEmpty()) {
            dailyMap = trackHourlyTotalPlaysService.getDailyTotalPlayDtoList(startDayTime,
                    endDayTime, ssbTracks).stream()
                .collect(Collectors.toMap(DailyPlaysSearchDto::getTrackId, DailyPlaysSearchDto::getTotalCount));
        }

        // chartHourlyList 생성
        List<SsbChartHourly> saveChartList = new ArrayList<>();

        double hourCount; // 현재 count
        double totalCount;// 최근 24시간
        double score;// 점수

        for (TrackTotalPlaysDto trackTotalPlaysDto : hourlyPlayList) {
            // 최근 24시간 플레이 횟수가 있을 경우
            totalCount = 0;
            hourCount = this.countDivCalc(trackTotalPlaysDto.getTotalCount(), DIV, NUM);

            Long trackId = trackTotalPlaysDto.getSsbTrack().getId();
            if (dailyMap != null && dailyMap.containsKey(trackId)) {
                // 최근 24시간 50%
                totalCount = this.countDivCalc(dailyMap.get(trackId), DIV, NUM);
                dailyMap.remove(trackId);
            }
            score = totalCount + hourCount;
            saveChartList.add(SsbChartHourly.create(trackTotalPlaysDto, score));
        }

        // score 내림차순
        // id는 오름차순
        List<SsbChartHourly> sortedList = saveChartList.stream().sorted(
                Comparator.comparingDouble(SsbChartHourly::getScore).reversed().thenComparing(o -> o.getSsbTrack().getId()))
            .collect(Collectors.toList());
        // score 기준 내림차순으로 id 기준으로 오름차순 정렬
//        Collections.sort(saveChartList, (o1, o2) -> {
//            if (o2.getScore() - o1.getScore() != 0.0) {
//                // 마이너스면은 내림차 순
//                // 플러스면은 오름차 순
//                double value = o2.getScore() - o1.getScore();
//                if (value < 0.0) {
//                    return -1;
//                }
//                return 1;
//            }
//            return (int) (o1.getSsbTrack().getId() - o2.getSsbTrack().getId());
//        });
        // 랭킹 순위 저장
        IntStream.range(0, sortedList.size()).forEach(i -> SsbChartHourly.updateRanking(sortedList.get(i), i + 1));

        trackChartHourlyService.saveAll(sortedList);
    }

    public List<TrackTotalPlaysDto> getHourlyPlaysDtos(int ranDayTime, int endDayTime, PageRequest pageRequest) {
        return trackHourlyTotalPlaysService.getHourlyPlayDtoList(ranDayTime,
            endDayTime,
            pageRequest);
    }

    @Transactional
    public void addChartDaily(int ranDayTime, int prevDayTime, PageRequest pageRequest) {

        // totalCount reversed 내림차순 정렬 후
        // thenComparing 으로 아이디 오름차순으로 정렬
        List<SsbChartDaily> chartDailyList = trackDailyTotalPlaysService.getDailyTotalPlays(ranDayTime,
                prevDayTime, pageRequest).stream()
            .map(SsbChartDaily::create).sorted(
                Comparator.comparingLong(SsbChartDaily::getTotalCount).reversed()
                    .thenComparing(o -> o.getSsbTrack().getId())).collect(
                Collectors.toList());

//        IntStream.range를 사용하여 리스트의 인덱스를 이용한 스트림 처리를 수행합니다. 이는 각 요소에 순위를 할당하는 과정을 보다 함수형 스타일로 처리
        IntStream.range(0, chartDailyList.size())
            .forEach(i -> SsbChartDaily.updateRanking(chartDailyList.get(i), i + 1));
        trackChartDailyService.saveAll(chartDailyList);
    }


    /**
     * count 나누기 계산 함수
     *
     * @param count
     * @param div
     * @param num
     * @return
     */
    public double countDivCalc(long count, double div, int num) {
        return (count / div) / num;
    }

    /**
     * chartIncluded 검색 후 반환
     *
     * @param user
     * @param ssbTrack
     * @param playDateTime
     * @return
     */
    public SsbChartIncludedPlays getChartIncludedPlay(User user, SsbTrack ssbTrack, LocalDateTime playDateTime) {
        return trackChatIncludedService.findOne(user, ssbTrack, playDateTime);
    }

    /**
     * chartIncluded 검색 후 반환
     *
     * @param user
     * @param ssbTrack
     * @param playDateTime
     * @return
     */
    public boolean existsIncludeChart(User user, SsbTrack ssbTrack, LocalDateTime playDateTime) {
        return trackChatIncludedService.existsIncludeChart(user, ssbTrack, DayTime.getDayTime(playDateTime));
    }


    /**
     * @param user
     * @param ssbTrack
     * @param id
     * @param token
     * @return
     */
    public SsbTrackAllPlayLogs getSsbTrackAllPlayLogs(User user, SsbTrack ssbTrack, String token,
        ChartStatus chartStatus) {
        return trackAllPlayLogService.findOne(user, ssbTrack, token, chartStatus);
    }

    // 현재 시간대에 공식 로그가 있는지 화인
    private void checkChartStatus(User user, SsbTrack ssbTrack, SsbTrackAllPlayLogs ssbTrackAllPlayLogs) {
        // 현재 시간대에 공식 로그가 있는지 화인
        boolean isChartLog = existsIncludeChart(user, ssbTrack,
            ssbTrackAllPlayLogs.getCreatedDateTime());
        // 공식 재생 여부 확인
        // true : 공식재생, false : 공식 재생 x
//        boolean isChartLog = includedLog == null;
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

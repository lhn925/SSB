package sky.Sss.domain.track.service.track;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.chart.SsbChartIncludedPlays;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.repository.track.TrackChartIncludedRepository;
import sky.Sss.domain.user.entity.User;
import sky.Sss.global.locationfinder.dto.UserLocationDto;
import sky.Sss.global.locationfinder.service.LocationFinderService;


/**
 * track getTrackPlayFile 횟수 count
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrackPlayCountService {

    private final LocationFinderService locationFinderService;
    private final TrackChartIncludedRepository trackChartIncludedRepository;

    @Transactional
    public SsbChartIncludedPlays save(SsbTrack ssbTrack, User user, String userAgent) {
        UserLocationDto userLocationDto = locationFinderService.findLocation();
        // 현재 시간 밀리초 출력
        long nowTimeMillis = System.currentTimeMillis();

        // Instant 객체 생성
        Instant instant = Instant.ofEpochMilli(nowTimeMillis);

        // 시스템의 기본 시간대를 사용하여 LocalDateTime 으로 변환
        LocalDateTime nowLocalDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());


        // 조회수 객체 생성
//        SsbChartIncludedPlays ssbChartIncludedPlays = SsbChartIncludedPlays.create(ssbTrack, user, userLocationDto,
//            userAgent);
//
//        // 저장
//        trackChartIncludedRepository.save(ssbChartIncludedPlays);
//        return ssbChartIncludedPlays;

        return null;
    }

    // 조회수

    /**
     * "
     * 1분 이상 들으면 플레이 횟수 증가 1분미만은 전부다,
     * 들어야함 정지를 해도 넘겨들어도 1분 이상 채우면 횟수 증가-> 이건 차트에 반영 안됨 단순 조회수
     * 조건을 충족한뒤 일정시간(10초) 이 지난후 집계
     *
     * 한시간에 한번 플레이는 차트에 반영
     * 플레이 도중 정지 및 넘김 시 해당 플레이 집계x -> 차트에 반영 되는 조회수
     * 차트 순위는 08~24 TOP 100은 24시간 조회수 50% + 1시간 이용량 50%
     * 01~07시 : 24시간 이용량 100%
     * 일간: 매일 낮 12시 기준 최근 24시간 이용량을 집계하며, 매일 13시 이후 업데이트된다.
     * 주간: 매주 월요일 낮 12시 기준 최근 7일 간 이용량을 집계하며, 매주 월요일 14시 이후 업데이트된다.
     * 월간: 매월 1일 낮 12시 기준 최근 1개월 간 이용량을 집계하며, 매월 1일 15시 이후 업데이트된다.
     * @param ssbTrack
     * @param user
     * @param playDateTime
     * @return
     */

/*

    public SsbChartIncludedPlays findOne(SsbTrack ssbTrack, User user, LocalDateTime playDateTime) {
        return trackChartIncludedRepository.checkSongPlayAtTime(user, ssbTrack,
            playDateTime).orElse(null);
    }
*/

}

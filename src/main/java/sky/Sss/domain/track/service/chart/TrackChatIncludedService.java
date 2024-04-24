package sky.Sss.domain.track.service.chart;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.track.chart.HourlyChartPlaysDto;
import sky.Sss.domain.track.entity.chart.SsbChartIncludedPlays;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.model.ChartStatus;
import sky.Sss.domain.track.model.PlayStatus;
import sky.Sss.domain.track.repository.chart.TrackChartIncludedRepository;
import sky.Sss.domain.user.entity.User;
import sky.Sss.global.utili.DayTime;


/**
 * track getTrackPlayFile 횟수 count
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrackChatIncludedService {

    private final TrackChartIncludedRepository chartIncludedRepository;

    @Transactional
    public void save(SsbChartIncludedPlays ssbChartIncludedPlays) {
        chartIncludedRepository.save(ssbChartIncludedPlays);
    }
    // 조회수

    public SsbChartIncludedPlays findOne(User user, SsbTrack ssbTrack, LocalDateTime playDateTime) {
        // 현재 날짜와 시간대를 YYYYMMddHH 형식으로 반납
        int dayTime = DayTime.getDayTime(playDateTime);
        Optional<SsbChartIncludedPlays> ssbChartIncludedPlays = chartIncludedRepository.checkPlayAtTime(user, ssbTrack,
            dayTime, ChartStatus.REFLECTED,
            PlayStatus.COMPLETED);
        return ssbChartIncludedPlays.orElse(null);
    }


    /** 
     * dayTime 에 공식 조회수 count 후 반환
     * @param dayTime
     * @return
     */
    public List<HourlyChartPlaysDto> hourlyChartFindByDayTime(int dayTime) {
        return chartIncludedRepository.getHourlyChartPlays(dayTime);
    }
}

package sky.Sss.domain.track.service.chart;


import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.track.chart.HourlyChartPlaysDto;
import sky.Sss.domain.track.entity.chart.SsbChartIncludedPlays;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.model.ChartStatus;
import sky.Sss.domain.track.model.PlayStatus;
import sky.Sss.domain.track.repository.chart.TrackChartIncludedRepository;
import sky.Sss.domain.user.entity.User;
import sky.Sss.global.redis.dto.RedisKeyDto;
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
    @CacheEvict(value = RedisKeyDto.REDIS_CACHE_INCLUDE_CHART_EXISTS_KEY, key = "#ssbChartIncludedPlays.ssbTrackAllPlayLogs.user.token + '_'+ #ssbChartIncludedPlays.ssbTrack.token + '_' +#ssbChartIncludedPlays.dayTime ", cacheManager = "contentCacheManager")
    public void save(SsbChartIncludedPlays ssbChartIncludedPlays) {
        chartIncludedRepository.save(ssbChartIncludedPlays);
    }
    // 조회수
    public SsbChartIncludedPlays findOne(User user, SsbTrack ssbTrack, LocalDateTime playDateTime) {
        // 현재 날짜와 시간대를 YYYYMMddHH 형식으로 반납
        int dayTime = DayTime.getDayTime(playDateTime);
        return getSsbChartIncludedPlays(
            user, ssbTrack, dayTime);
    }

    public SsbChartIncludedPlays getSsbChartIncludedPlays(User user, SsbTrack ssbTrack, int dayTime) {
        return chartIncludedRepository.checkPlayAtTime(user, ssbTrack,
            dayTime, ChartStatus.REFLECTED,
            PlayStatus.COMPLETED).orElse(null);
    }


    @Cacheable(value = RedisKeyDto.REDIS_CACHE_INCLUDE_CHART_EXISTS_KEY, key = "#user.token + '_'+ #ssbTrack.token + '_' +#dayTime ", cacheManager = "contentCacheManager")
    public boolean existsIncludeChart(User user, SsbTrack ssbTrack, int dayTime) {
        return getSsbChartIncludedPlays(user, ssbTrack, dayTime) == null;
    }

    /**
     * dayTime 에 공식 조회수 count 후 반환
     *
     * @param dayTime
     * @return
     */
    public List<HourlyChartPlaysDto> hourlyChartFindByDayTime(int dayTime) {
        return chartIncludedRepository.getHourlyChartPlays(dayTime);
    }
}

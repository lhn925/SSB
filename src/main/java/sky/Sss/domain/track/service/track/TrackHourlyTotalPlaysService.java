package sky.Sss.domain.track.service.track;


import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.track.chart.DailyPlaysSearchDto;
import sky.Sss.domain.track.dto.track.chart.DailyTotalPlaysCreateDto;
import sky.Sss.domain.track.dto.track.chart.TrackTotalPlaysDto;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.log.SsbTrackHourlyTotalPlays;
import sky.Sss.domain.track.repository.track.log.TrackHourlyTotalPlaysRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrackHourlyTotalPlaysService {

    private final TrackHourlyTotalPlaysRepository trackHourlyTotalPlaysRepository;


    /**
     * ssbTrackHourlyTotalPlays 생성 및
     * 생성 값 반환
     *
     * @param hourlyTotalPlays
     * @return
     */
    @Transactional
    public void saveAll(List<SsbTrackHourlyTotalPlays> hourlyTotalPlays) {
        trackHourlyTotalPlaysRepository.saveAll(hourlyTotalPlays);
    }

    /**
     * ranDayTime 에 해당하는 데이터 조회 후 dto 리스트 반환
     * ssbChartHourly 와 join 조회 후 이전 시간대의 ranking 순위 같이 반환
     *
     * @param ranDayTime
     * @param prevDayTime
     * @param pageRequest
     * @return
     */
    public List<TrackTotalPlaysDto> getHourlyPlayDtoList(int ranDayTime, int prevDayTime, PageRequest pageRequest) {
        return trackHourlyTotalPlaysRepository.getHourlyPlays(ranDayTime, prevDayTime, pageRequest);
    }

    /**
     * startDayTime endTime와 ssbTracks에 해당 하는 데이터 조회후 리스트 반환
     *
     * @param startDayTime
     * @param endDayTime
     * @param ssbTracks
     * @return
     */
    public List<DailyPlaysSearchDto> getDailyTotalPlayDtoList(int startDayTime, int endDayTime, List<SsbTrack> ssbTracks) {
        return trackHourlyTotalPlaysRepository.getDailyPlaysAndTrackId(startDayTime, endDayTime, ssbTracks);
    }

    /**
     * startDayTime endTime 에 해당하는 데이터 조회 후 DailyPlaysSearchDto 리스트 반환
     *
     * @param startDayTime
     * @param endDayTime
     * @return
     */
    public List<DailyTotalPlaysCreateDto> getDailyTotalPlayDtoList(int startDayTime, int endDayTime) {
        return trackHourlyTotalPlaysRepository.getDailyPlays(startDayTime, endDayTime);
    }

}

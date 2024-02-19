package sky.Sss.domain.track.service.track;


import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.track.chart.TrackTotalPlaysDto;
import sky.Sss.domain.track.entity.track.log.SsbTrackDailyTotalPlays;
import sky.Sss.domain.track.repository.track.log.TrackDailyTotalPlaysRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrackDailyTotalPlaysService {

    private final TrackDailyTotalPlaysRepository trackDailyTotalPlaysRepository;
    /**
     * SsbTrackDailyTotalPlays 생성 및
     * 생성 값 반환
     *
     * @param dailyTotalPlays
     * @return
     */
    @Transactional
    public void saveAll(List<SsbTrackDailyTotalPlays> dailyTotalPlays) {
        trackDailyTotalPlaysRepository.saveAll(dailyTotalPlays);
    }

    public List<TrackTotalPlaysDto> getDailyTotalPlays(int ranDayTime, int prevDayTime, PageRequest pageRequest) {
        return trackDailyTotalPlaysRepository.getDailyPlays(ranDayTime, prevDayTime, pageRequest);
    }
}

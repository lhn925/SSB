package sky.Sss.domain.track.service.track;


import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.chart.SsbChartIncludedPlays;
import sky.Sss.domain.track.entity.chart.SsbTrackAllPlayLogs;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.model.ChartStatus;
import sky.Sss.domain.track.model.Hour;
import sky.Sss.domain.track.model.PlayStatus;
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
public class TrackPlayIncludedService {

    private final LocationFinderService locationFinderService;
    private final TrackChartIncludedRepository chartIncludedRepository;

    @Transactional
    public SsbChartIncludedPlays save(SsbTrackAllPlayLogs ssbTrackAllPlayLogs) {

        return null;
    }

    // 조회수

    public SsbChartIncludedPlays findOne(User user, SsbTrack ssbTrack, LocalDateTime playDateTime) {
        Hour hour = Hour.findByHour(playDateTime.getHour());
        LocalDate createdDate = playDateTime.toLocalDate();
        Optional<SsbChartIncludedPlays> ssbChartIncludedPlays = chartIncludedRepository.checkPlayAtTime(user, ssbTrack,
            hour.getValue(), ChartStatus.REFLECTED,
            PlayStatus.COMPLETED, createdDate);
        return ssbChartIncludedPlays.orElse(null);
    }

}

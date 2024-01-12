package sky.Sss.domain.track.repository.track;


import org.springframework.data.jpa.repository.JpaRepository;
import sky.Sss.domain.track.entity.chart.SsbTrackAllPlayLogs;

public interface TrackAllPlayLogRepository extends JpaRepository<SsbTrackAllPlayLogs, Long> {

}

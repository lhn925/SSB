package sky.Sss.domain.track.repository.track;


import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.track.entity.chart.SsbTrackAllPlayLogs;

public interface TrackAllPlayLogRepository extends JpaRepository<SsbTrackAllPlayLogs, Long> {

}

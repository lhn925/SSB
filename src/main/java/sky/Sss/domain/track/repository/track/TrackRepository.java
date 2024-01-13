package sky.Sss.domain.track.repository.track;

import org.springframework.data.jpa.repository.JpaRepository;
import sky.Sss.domain.track.entity.track.SsbTrack;

public interface TrackRepository extends JpaRepository<SsbTrack, Long> {

}

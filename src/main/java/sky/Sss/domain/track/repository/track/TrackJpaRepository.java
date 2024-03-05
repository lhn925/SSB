package sky.Sss.domain.track.repository.track;

import org.springframework.data.jpa.repository.JpaRepository;
import sky.Sss.domain.track.entity.track.SsbTrack;

public interface TrackJpaRepository extends JpaRepository<SsbTrack, Long> {
}

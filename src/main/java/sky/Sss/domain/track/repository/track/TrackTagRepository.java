package sky.Sss.domain.track.repository.track;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import sky.Sss.domain.track.entity.track.SsbTrackTags;

public interface TrackTagRepository extends JpaRepository<SsbTrackTags, Long> {

    Optional<SsbTrackTags> findByTag(String tag);

    Set<SsbTrackTags> findByIdIn(Set<Long> ids);
}

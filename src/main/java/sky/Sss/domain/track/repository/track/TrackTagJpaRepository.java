package sky.Sss.domain.track.repository.track;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import sky.Sss.domain.track.entity.track.SsbTrackTags;

public interface TrackTagJpaRepository extends JpaRepository<SsbTrackTags, Long> {



    Optional<SsbTrackTags> findByTag(String tag);
    List<SsbTrackTags> findAllByTagIn(Set<String> tagsList);


    Set<SsbTrackTags> findByIdIn(Set<Long> ids);
}

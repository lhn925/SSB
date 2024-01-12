package sky.Sss.domain.track.repository.playList;

import org.springframework.data.jpa.repository.JpaRepository;
import sky.Sss.domain.track.entity.playList.SsbPlayListTagLink;
import sky.Sss.domain.track.entity.track.SsbTrackTagLink;

public interface PlayListTagLinkRepository extends JpaRepository<SsbPlayListTagLink, Long> {


}

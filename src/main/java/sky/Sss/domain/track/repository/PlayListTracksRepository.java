package sky.Sss.domain.track.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sky.Sss.domain.track.entity.playList.SsbPlayListTracks;

public interface PlayListTracksRepository extends JpaRepository<SsbPlayListTracks,Long> {

}

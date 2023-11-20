package sky.Sss.domain.track.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;

public interface PlayListSettingRepository extends JpaRepository<SsbPlayListSettings,Long> {

}

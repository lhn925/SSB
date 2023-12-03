package sky.Sss.domain.track.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.track.entity.playList.SsbPlayListTracks;

public interface PlayListTracksRepository extends JpaRepository<SsbPlayListTracks,Long> {



    @Modifying(clearAutomatically = true)
    @Query("delete from SsbPlayListTracks s where s.ssbPlayListSettings.id = :settingId")
    void deleteBySettingsId (@Param("settingId") Long settingId);
}

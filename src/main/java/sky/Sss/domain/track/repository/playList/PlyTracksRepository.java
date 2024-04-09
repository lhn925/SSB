package sky.Sss.domain.track.repository.playList;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.track.entity.playList.SsbPlayListTracks;

public interface PlyTracksRepository extends JpaRepository<SsbPlayListTracks,Long> {



    @Modifying(clearAutomatically = true)
    @Query("delete from SsbPlayListTracks s where s.ssbPlayListSettings.id = :settingId")
    void deleteBySettingsId (@Param("settingId") long settingId);



    @Query("select p from SsbPlayListTracks  p where p.ssbPlayListSettings.id = :settingId ")
    List<SsbPlayListTracks> findByPlyTracks(@Param("settingId") long settingId, Sort sort);


    @Query("select p from SsbPlayListTracks  p where p.id = in(:ids) and p.ssbPlayListSettings.id = :settingId ")
    List<SsbPlayListTracks> findByPlyTracks(@Param("ids") List<Long> ids ,@Param("settingId") long settingId, Sort sort);

}

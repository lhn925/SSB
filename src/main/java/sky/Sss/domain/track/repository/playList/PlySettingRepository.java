package sky.Sss.domain.track.repository.playList;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;
import sky.Sss.domain.user.entity.User;

public interface PlySettingRepository extends JpaRepository<SsbPlayListSettings, Long> {


    @Query("select p from SsbPlayListSettings p  where p.id= :id "
        + " and p.token = :token and p.user =:user and p.isStatus = :isStatus")
    Optional<SsbPlayListSettings> findOne(@Param("id") Long id, @Param("token") String token, @Param("user") User user,@Param("isStatus") Boolean isStatus);
}

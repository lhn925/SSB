package sky.Sss.domain.track.repository.playList;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;
import sky.Sss.domain.track.entity.playList.SsbPlyLikes;
import sky.Sss.domain.user.entity.User;

public interface PlyLikesRepository extends JpaRepository<SsbPlyLikes, Long> {



    @Query("select s from SsbPlyLikes s where s.ssbPlayListSettings.id = :id and s.user = :user ")
    Optional<SsbPlyLikes> findBySettingsIdAndUser(@Param("id") long id,@Param("user") User user);

    @Query("select s from SsbPlyLikes s where s.ssbPlayListSettings =:settings and s.user =:user ")
    Optional<SsbPlyLikes> findByPlyIdAndUser(@Param("settings") SsbPlayListSettings settings, @Param("user") User user);

    @Query("select s from SsbPlyLikes s where s.ssbPlayListSettings.token = :token")
    List<SsbPlyLikes> getListPlyByToken(@Param("token") String token);

    @Query("select count(s.id) from SsbPlyLikes s where s.ssbPlayListSettings.id = :trackId")
    Integer countByTrackId(@Param("trackId") String trackId);

    @Query("select u from SsbPlyLikes s join s.user u where s.ssbPlayListSettings.token = :token")
    List<User> getUserList(@Param("token") String token);

}

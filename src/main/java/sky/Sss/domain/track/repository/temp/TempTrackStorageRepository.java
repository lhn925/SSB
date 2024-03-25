package sky.Sss.domain.track.repository.temp;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.track.entity.temp.TempTrackStorage;
import sky.Sss.domain.user.entity.User;

public interface TempTrackStorageRepository extends JpaRepository<TempTrackStorage, Long> {


    @Query("select t from TempTrackStorage t where t.id=:id and t.token = :token and t.user =:user and t.isPrivacy =:isPrivacy and t.isPlayList =:isPlayList")
    Optional<TempTrackStorage> findOne(@Param("id") Long id, @Param("token") String token, @Param("user") User user,
        @Param("isPrivacy") boolean isPrivacy, @Param("isPlayList") boolean isPlayList);

    @Query("select t from TempTrackStorage t where  t.user =:user and t.isPrivacy =:isPrivacy and t.isPlayList =:isPlayList and t.token in (:tokens) and t.id in (:ids)")
    List<TempTrackStorage> findByUid(@Param("user") User user, @Param("tokens") List<String> tokens,
        @Param("ids") List<Long> ids, @Param("isPrivacy") boolean isPrivacy, @Param("isPlayList") boolean isPlayList);


    @Query("select t from TempTrackStorage t where t.user.id = :uid and t.isPrivacy =:isPrivacy and t.isPlayList =:isPlayList ")
    List<TempTrackStorage> findByUid(@Param("uid") long uid , @Param("isPrivacy")  boolean isPrivacy, @Param("isPlayList") boolean isPlayList);
}

package sky.Sss.domain.track.repository.temp;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import sky.Sss.domain.track.entity.TempTrackStorage;
import sky.Sss.domain.user.entity.User;

public interface TempTrackStorageRepository extends JpaRepository<TempTrackStorage, Long> {


    @Query("select t from TempTrackStorage t where t.id=:id and t.token = :token and t.sessionId =:sessionId and t.user =:user")
    Optional<TempTrackStorage> findOne(@Param("id") Long id, @Param("token") String token,
        @Param("sessionId") String sessionId, @Param("user") User user);

    @Query("select t from TempTrackStorage t where t.sessionId =:sessionId and t.user =:user and t.token in (:tokens) and t.id in (:ids)")
    List<TempTrackStorage> findBySessionId(@Param("sessionId") String sessionId, @Param("user") User user,@Param("tokens") List<String> tokens, @Param("ids") List<Long> ids);

}

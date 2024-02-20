package sky.Sss.domain.track.repository.track;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.SsbTrackLikes;
import sky.Sss.domain.user.entity.User;

public interface TrackLikesRepository extends JpaRepository<SsbTrackLikes, Long> {


    Optional<SsbTrackLikes> findBySsbTrackAndUser(SsbTrack ssbTrack,User user);


    boolean existsBySsbTrackAndUser(SsbTrack ssbTrack, User user);



    @Query("select count(s.id) from SsbTrackLikes s where s.ssbTrack.token = :token")
    Integer countByTrackToken(@Param("token")String token);
    @Query("select count(s.id) from SsbTrackLikes s where s.ssbTrack.id = :trackId")
    Integer countByTrackId(@Param("trackId")String trackId);

}

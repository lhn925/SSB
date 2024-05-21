package sky.Sss.domain.track.repository.track;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.SsbTrackLikes;
import sky.Sss.domain.user.entity.User;

public interface TrackLikesRepository extends JpaRepository<SsbTrackLikes, Long> {


    Optional<SsbTrackLikes> findBySsbTrackIdAndUser(long trackId,User user);
    Optional<SsbTrackLikes> findBySsbTrackAndUser(SsbTrack ssbTrack,User user);


    @Query("select s from SsbTrackLikes s join fetch s.user u join fetch s.ssbTrack t where s.user.id = :uid and s.ssbTrack.token = :trackToken")
    Optional<SsbTrackLikes> findByLikeByTrackToken(@Param("trackToken") String trackToken, @Param("uid") long uid);

    List<SsbTrackLikes> findAllByUser(User user);

    boolean existsBySsbTrackAndUser(SsbTrack ssbTrack, User user);


    @Query("select s.user from SsbTrackLikes s join s.user "
        + "where s.ssbTrack.token = :token")
    List<User> getUserList(@Param("token") String token);

    @Query("select s from SsbTrackLikes s join fetch s.user where s.ssbTrack.token = :token")
    List<SsbTrackLikes> getTrackLikesByToken(@Param("token") String token);
    @Query("select count(s.id) from SsbTrackLikes s where s.ssbTrack.id = :trackId")
    Integer countByTrackId(@Param("trackId")String trackId);

}

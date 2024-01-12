package sky.Sss.domain.track.repository.track;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.user.entity.User;

public interface TrackRepository extends JpaRepository<SsbTrack, Long> {

    /**
     * 업로드 곡 길이 총 합
     *
     * @param user
     * @return
     */
    @Query("select COALESCE(sum(s.trackLength),0) from SsbTrack s where s.user = :user and s.isStatus =:isStatus")
    Integer getTotalTrackLength(@Param("user") User user, @Param("isStatus") Boolean isStatus);

    @Query("select s from SsbTrack s where s.id =:id and s.user =:user and s.token =:token and s.isStatus =:isStatus")
    Optional<SsbTrack> findOne(@Param("id") Long id, @Param("user") User user, @Param("token") String token,
        @Param("isStatus") Boolean isStatus);

    @Query("select s from SsbTrack s where s.id =:id and s.token =:token and s.isStatus =:isStatus")
    Optional<SsbTrack> findOne(@Param("id") Long id, @Param("token") String token,
        @Param("isStatus") Boolean isStatus);



    @Query("select s from SsbTrack s join fetch s.user where s.id = :id and s.isStatus =:isStatus")
    Optional<SsbTrack> findByIdJoinUser(@Param("id") Long id, @Param("isStatus") Boolean isStatus);


    @Query("select s from SsbTrack s join fetch s.tags where s.id =:id and s.user =:user and s.token =:token and s.isStatus =:isStatus")
    Optional<SsbTrack> findOneWithTags(@Param("id") Long id, @Param("user") User user, @Param("token") String token,
        @Param("isStatus") Boolean isStatus);

}

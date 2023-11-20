package sky.Sss.domain.track.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.user.entity.User;

public interface TrackRepository extends JpaRepository<SsbTrack, Long> {

    List<SsbTrack> findByUser(User user);


    /**
     * 업로드 곡 길이 총 합
     * @param user
     * @return
     */
    @Query("select COALESCE(sum(s.trackLength),0) from SsbTrack s where s.user = :user")
    Integer getTotalTrackLength(@Param("user") User user);
}

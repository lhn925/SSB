package sky.Sss.domain.track.repository.track;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.track.dto.common.TargetInfoDto;
import sky.Sss.domain.track.dto.track.TrackInfoRepDto;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.user.entity.User;

public interface TrackQueryRepository extends JpaRepository<SsbTrack, Long> {

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

    Optional<SsbTrack> findByIdAndIsStatus(Long id, Boolean isStatus);

    @Query("select s from SsbTrack s join fetch s.user where s.id = :id and s.isStatus =:isStatus and s.token=:token")
    Optional<SsbTrack> findByIdJoinUser(@Param("id") Long id, @Param("token") String token,
        @Param("isStatus") Boolean isStatus);

    @Query("select s from SsbTrack s join fetch s.user where s.id = :id and s.isStatus =:isStatus")
    Optional<SsbTrack> findByIdJoinUser(@Param("id") Long id,
        @Param("isStatus") Boolean isStatus);

    @Query("select s from SsbTrack s join fetch s.tags where s.id =:id and s.user =:user and s.token =:token and s.isStatus =:isStatus")
    Optional<SsbTrack> findOneWithTags(@Param("id") Long id, @Param("user") User user, @Param("token") String token,
        @Param("isStatus") Boolean isStatus);

    @Query(
        "select new sky.Sss.domain.track.dto.track.TrackInfoRepDto(s.id,s.token,s.title,s.coverUrl,u.userName,s.trackLength,s.createdDateTime) "
            + " from SsbTrack s join fetch User u"
            + " on s.user = u "
            + " where s.token in (:token) and s.user =:user and s.isStatus =:isStatus")
    List<TrackInfoRepDto> findAllByToken(@Param("token") List<String> tokenList,@Param("user") User user,@Param("isStatus") boolean isStatus);

    @Query(
        "select new sky.Sss.domain.track.dto.common.TargetInfoDto(s.id,s.token,s.title,s.user,s.isPrivacy) from SsbTrack s join fetch User u "
            + " on s.user.id = u.id "
            + " where s.id = :id and s.token =:token and s.isStatus =:isStatus")
    Optional<TargetInfoDto> getTargetInfoDto(@Param("id") long id, @Param("token") String token,
        @Param("isStatus") boolean isStatus);


}

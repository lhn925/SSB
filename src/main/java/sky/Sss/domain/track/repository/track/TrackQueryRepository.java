package sky.Sss.domain.track.repository.track;

import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.track.dto.common.rep.TargetInfoDto;
import sky.Sss.domain.track.dto.track.rep.TrackUploadCountDto;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Status;

public interface TrackQueryRepository extends JpaRepository<SsbTrack, Long> {

    /**
     * 업로드 곡 길이 총 합
     *
     * @param user
     * @return
     */
    @Query("select COALESCE(sum(s.trackLength),0) from SsbTrack s where s.user = :user and s.isStatus =:isStatus")
    Integer getTotalTrackLength(@Param("user") User user, @Param("isStatus") boolean isStatus);

    @Query("select s from SsbTrack s where s.id =:id and s.user =:user and s.token =:token and s.isStatus =:isStatus")
    Optional<SsbTrack> findOne(@Param("id") Long id, @Param("user") User user, @Param("token") String token,
        @Param("isStatus") boolean isStatus);

    @Query("select s from SsbTrack s where s.id =:id and s.token =:token and s.isStatus =:isStatus")
    Optional<SsbTrack> findOne(@Param("id") Long id, @Param("token") String token,
        @Param("isStatus") boolean isStatus);

    List<SsbTrack> findAllByIdInAndIsStatus(Set<Long> ids, Boolean isStatus);



    @Query("select new sky.Sss.domain.track.dto.track.rep.TrackUploadCountDto(u.id, count(s.id)) " +
        "from User u left join SsbTrack s on u = s.user " +
        "where u = :user and (s.isStatus = :isStatus or s.id is null) and (s.isPrivacy = false or s.id is null) " +
        "group by u.id")
    TrackUploadCountDto getUserUploadCount(@Param("user") User user, @Param("isStatus") boolean isStatus);


    @Query("select new sky.Sss.domain.track.dto.track.rep.TrackUploadCountDto(u.id,COALESCE(count(s.id),0)) "
        + " from User u left join SsbTrack s"
        + " on u = s.user "
        + "where u in (:users) and (s.isStatus = :isStatus or s.id is null) and  (s.isPrivacy = false or s.id is null) group by u.id ")
    List<TrackUploadCountDto> getUsersUploadCount(@Param("users") List<User> users, @Param("isStatus") boolean isStatus);


    @Query("select new sky.Sss.domain.track.dto.track.rep.TrackUploadCountDto(u.id, count(s.id)) " +
        "from User u left join SsbTrack s on u = s.user " +
        "where u = :user and (s.isStatus = :isStatus or s.id is null) " +
        "group by u.id")
    TrackUploadCountDto getMyUploadCount(@Param("user") User user, @Param("isStatus") boolean isStatus);

//    @Query(
//        "select new sky.Sss.domain.track.dto.track.common.TrackInfoSimpleDto(s.id,s.token,s.title,u,s.trackLength,s.coverUrl,s.isPrivacy,s.createdDateTime)"
//            + " from SsbTrack s join fetch User u on s.user = u where s.id =:id and s.isStatus =:isStatus")
//    Optional<TrackInfoSimpleDto> getTrackInfoSimpleDto(@Param("id") Long id,
//        @Param("isStatus") boolean isStatus);


    /*
     * 쿼리르 줄이기 위해서 Like join
     * 비공개 여부 확인
     *
     *  @param ids
     * @param likedUserId
     * @param isStatus
     * @return
     */
/*
    @Query(
        "select new sky.Sss.domain.track.dto.track."
            + "common.TrackInfoSimpleDto(s.id,s.token,s.title,s.trackLength,s.coverUrl,s.isPrivacy,u,:likeUid,l,f,s.createdDateTime)"
            + " from SsbTrack s join fetch User u on s.user = u "
            + " left outer join SsbTrackLikes l on s.id = l.ssbTrack.id and (l.user.id = :likeUid or l.user is null) "
            + " left outer join UserFollows f on s.user.id = f.followingUser.id and (f.followerUser.id = :likeUid or f.followerUser is null) "
            + " where s.id in (:ids) and s.isStatus =:isStatus and (s.isPrivacy = false or"
            + " (s.isPrivacy = true and s.user.id = :likeUid) )")
    List<TrackInfoSimpleDto> getTrackInfoSimpleDtoList(@Param("ids") Set<Long> ids,
        @Param("likeUid") long likeUid,
        @Param("isStatus") boolean isStatus);
*/


/*
    @Query(
        "select new sky.Sss.domain.track.dto.track.common."
            + "TrackInfoSimpleDto(s.id,s.title,u,s.trackLength,s.coverUrl,s.isPrivacy,s.createdDateTime)"
            + " from SsbTrack s join fetch User u on s.user = u "
            + "where s.id in (:ids) and s.isStatus =:isStatus and s.isPrivacy = :isPrivacy")
    List<TrackInfoSimpleDto> getTrackInfoSimpleDtoList(@Param("ids") Set<Long> ids,
        @Param("isStatus") boolean isStatus, @Param("isPrivacy") boolean isPrivacy);
*/

    @Query("select s from SsbTrack s join fetch s.user where s.id = :id and s.isStatus =:isStatus ")
    Optional<SsbTrack> findByIdAndIsStatus(Long id, boolean isStatus);

    @Query("select s from SsbTrack s join fetch s.user where s.id = :id and s.isStatus =:isStatus and s.token=:token")
    Optional<SsbTrack> findByIdJoinUser(@Param("id") Long id, @Param("token") String token,
        @Param("isStatus") boolean isStatus);

    @Query("select s from SsbTrack s join fetch s.user where s.id = :id and s.isStatus =:isStatus ")
    Optional<SsbTrack> findByIdJoinUser(@Param("id") long id,
        @Param("isStatus") boolean isStatus);

    @Query("select s from SsbTrack s join fetch s.user where s.id in (:ids) and s.isStatus =:isStatus ")
    List<SsbTrack> findAllByIdsJoinUser(@Param("ids") Set<Long> ids,
        @Param("isStatus") boolean isStatus);


    @Query("select s from SsbTrack s join fetch s.tags where s.id =:id and s.user =:user and s.token =:token and s.isStatus =:isStatus")
    Optional<SsbTrack> findOneWithTags(@Param("id") Long id, @Param("user") User user, @Param("token") String token,
        @Param("isStatus") boolean isStatus);

    @Query(
        "select s "
            + " from SsbTrack s join fetch User u"
            + " on s.user = u "
            + " where s.token in (:token) and s.user =:user and s.isStatus =:isStatus")
    List<SsbTrack> findAllByToken(@Param("token") List<String> tokenList, @Param("user") User user,
        @Param("isStatus") boolean isStatus);

    @Query(
        "select new sky.Sss.domain.track.dto.common.rep.TargetInfoDto(s.id,s.token,s.title,s.user,s.isPrivacy) from SsbTrack s join fetch User u "
            + " on s.user.id = u.id "
            + " where s.id = :id and s.token =:token and s.isStatus =:isStatus")
    Optional<TargetInfoDto> getTargetInfoDto(@Param("id") long id, @Param("token") String token,
        @Param("isStatus") boolean isStatus);

    @Query(
        "select new sky.Sss.domain.track.dto.common.rep.TargetInfoDto(s.id,s.token,s.title,s.user,s.isPrivacy) from SsbTrack s join fetch User u "
            + " on s.user.id = u.id "
            + " where s.id = :id and s.isStatus =:isStatus")
    Optional<TargetInfoDto> getTargetInfoDto(@Param("id") long id,
        @Param("isStatus") boolean isStatus);


}

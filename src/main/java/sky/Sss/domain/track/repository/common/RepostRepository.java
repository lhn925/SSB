package sky.Sss.domain.track.repository.common;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.track.dto.common.RepostInfoDto;
import sky.Sss.domain.track.entity.SsbRepost;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.ContentsType;

public interface RepostRepository extends JpaRepository<SsbRepost, Long> {


    @Query("select r.user from SsbRepost r join fetch User u on r.user = u where r.contentsId = :contentsId and r.contentsType = :contentsType")
    List<User> getUserList(@Param("contentsId") long contentsId, @Param("contentsType") ContentsType contentsType);

    @Query("select r from SsbRepost r where r.contentsId = :contentsId and r.contentsType = :contentsType")
    List<SsbRepost> getRepostList(@Param("contentsId") long contentsId, @Param("contentsType") ContentsType contentsType);

    @Modifying(flushAutomatically = true)
    @Query("update SsbRepost r set  r.isPrivacy =:isPrivacy where r.contentsId =:contentsId and r.contentsType =:contentsType")
   void  privacyBatchUpdate(@Param("contentsId") long contentsId,@Param("isPrivacy") boolean isPrivacy ,@Param("contentsType") ContentsType contentsType);

    @Query("select r from SsbRepost r where r.user.id = :uid and r.contentsId =:contentsId and r.contentsType =:contentsType")
    Optional<SsbRepost> findOne(@Param("uid") long uid, @Param("contentsId") long contentsId,
        @Param("contentsType") ContentsType contentsType);


    @Query("select r from SsbRepost r where r.id = :id and r.token = :token and r.user = :user")
    Optional<SsbRepost> findOne(@Param("id") long id, @Param("token") String token, @Param("user") User user);

    @Query(
        "select new sky.Sss.domain.track.dto.common.RepostInfoDto(s.token,s.id,r) from SsbRepost r join SsbTrack s "
            + " on r.contentsId = s.id "
            + "where r.id = :id and r.token = :token and r.user = :user ")
    Optional<RepostInfoDto> findOneJoinType(@Param("id") long id, @Param("token") String token,
        @Param("user") User user);

    @Query("select new sky.Sss.domain.track.dto.common.RepostInfoDto(s.token,s.id,r) from SsbRepost r join SsbPlayListSettings s on r.contentsId = s.id where r.id = :id and r.token = :token and r.user = :user ")
    Optional<RepostInfoDto> findOneJoinPlayList(@Param("id") long id, @Param("token") String token,
        @Param("user") User user);
}

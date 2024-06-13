package sky.Sss.domain.track.repository.playList;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.track.dto.common.rep.TargetInfoDto;
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;
import sky.Sss.domain.user.entity.User;

public interface PlyQueryRepository extends JpaRepository<SsbPlayListSettings, Long> {


    @Query("select s from SsbPlayListSettings s where s.id =:id and s.user =:user and s.token =:token and s.isStatus =:isStatus")
    Optional<SsbPlayListSettings> findOne(@Param("id") Long id, @Param("user") User user, @Param("token") String token,
        @Param("isStatus") Boolean isStatus);

    @Query("select s from SsbPlayListSettings s where s.id =:id and s.token =:token and s.isStatus =:isStatus")
    Optional<SsbPlayListSettings> findOne(@Param("id") Long id, @Param("token") String token,
        @Param("isStatus") Boolean isStatus);

    Optional<SsbPlayListSettings> findByIdAndIsStatus(Long id, Boolean isStatus);

    @Query("select s from SsbPlayListSettings s join fetch s.user where s.id = :id and s.isStatus =:isStatus")
    Optional<SsbPlayListSettings> findByJoinUser(@Param("id") Long id, @Param("isStatus") Boolean isStatus);



    @Query("select s from SsbPlayListSettings s join fetch s.user where s.id = :id and s.token = :token and s.isStatus =:isStatus")
    Optional<SsbPlayListSettings> findByJoinUser(@Param("id") Long id, @Param("token") String token,
        @Param("isStatus") Boolean isStatus);


    @Query("select s from SsbPlayListSettings s join fetch s.tags where s.id =:id and s.user =:user and s.token =:token and s.isStatus =:isStatus")
    Optional<SsbPlayListSettings> findOneWithTags(@Param("id") Long id, @Param("user") User user,
        @Param("token") String token,
        @Param("isStatus") Boolean isStatus);


    @Query("select new sky.Sss.domain.track.dto.common.rep.TargetInfoDto(s.id,s.token,s.title,u,s.isPrivacy) from SsbPlayListSettings s "
        + "join fetch User u on s.user = u where s.id = :id and s.token =:token and s.isStatus =:isStatus")
    Optional<TargetInfoDto> getTargetInfoDto(@Param("id") long id, @Param("token") String token,
        @Param("isStatus") boolean isStatus);

}

package sky.Sss.domain.track.repository.playList.reply;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.track.dto.common.TargetInfoDto;
import sky.Sss.domain.track.dto.common.ReplyRmInfoDto;
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;
import sky.Sss.domain.track.entity.playList.reply.SsbPlyReply;

public interface PlyReplyRepository extends JpaRepository<SsbPlyReply, Long> {


    @Query(" select new sky.Sss.domain.track.dto.common.ReplyRmInfoDto(r.id,r.token,r.user.id,s.id,s.token) "
        + " from SsbPlyReply r join fetch SsbPlayListSettings s "
        + " on r.ssbPlayListSettings.id = s.id "
        + " where r.token = :token "
        + " and r.id =:id "
        + " or r.parentId =:id ")
    List<ReplyRmInfoDto> getReplyRmInfoDtoList(@Param("id") Long id, @Param("token") String token);

    @Query("select r from SsbPlyReply r join fetch r.ssbPlayListSettings "
        + "where r.token = :token "
        + "and r.id =:id "
        + "or r.parentId =:id ")
    List<SsbPlyReply> findListAndSubReplies(@Param("id") Long id, @Param("token") String token);

    Optional<SsbPlyReply> findByIdAndSsbPlayListSettings(Long id, SsbPlayListSettings ssbPlayListSettings);

    List<SsbPlyReply> findAllByParentId(Long parentId);

    @Query("select r from SsbPlyReply r join fetch r.user where r.id = :id")
    Optional<SsbPlyReply> findOne(@Param("id") Long id);

    @Query("select coalesce(max(r.replyOrder),0) from SsbPlyReply r where r.parentId =:parentId and r.ssbPlayListSettings =:ssbPlayListSettings ")
    Integer findMaxOrderByParentId(@Param("parentId") Long parentId,
        @Param("ssbPlayListSettings") SsbPlayListSettings ssbPlayListSettings);


    @Query(
        "select new sky.Sss.domain.track.dto.common.TargetInfoDto(r.id,r.token,r.contents,r.user,r.ssbPlayListSettings.id) "
            + " from SsbPlyReply r join fetch User u "
            + " on r.user.id = u.id "
            + " where r.id = :id and r.token =:token ")
    Optional<TargetInfoDto> getTargetInfoDto(@Param("id") long id, @Param("token") String token);

}

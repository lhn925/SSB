package sky.Sss.domain.track.repository.track.reply;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.track.dto.common.rep.TargetInfoDto;
import sky.Sss.domain.track.dto.common.reply.ReplyRmInfoDto;
import sky.Sss.domain.track.dto.track.reply.TrackRedisReplyDto;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.reply.SsbTrackReply;

public interface TrackReplyRepository extends JpaRepository<SsbTrackReply, Long> {


    @Query(
        "select new sky.Sss.domain.track.dto.common.ReplyRmInfoDto(r.id,r.token,r.user.id ,s.id,s.token) "
            + " from SsbTrackReply r join fetch SsbTrack s "
            + " on r.ssbTrack.id = s.id "
            + " where r.token = :token "
            + " and r.id =:id "
            + " or r.parentId =:id ")
    List<ReplyRmInfoDto> getReplyRmInfoDtoList(@Param("id") Long id, @Param("token") String token);

    @Query("select r from SsbTrackReply r join fetch r.ssbTrack "
        + "where r.token = :token "
        + "and r.id =:id "
        + "or r.parentId =:id ")
    List<SsbTrackReply> findListAndSubReplies(@Param("id") Long id, @Param("token") String token);


    @Query("select r from SsbTrackReply r join fetch r.ssbTrack where r.ssbTrack.token = :trackToken")
    List<SsbTrackReply> getRepliesByTrackToken(@Param("trackToken") String trackToken);


    @Query("select new sky.Sss.domain.track.dto.track.reply.TrackRedisReplyDto(r) from "
        + "SsbTrackReply r join fetch r.ssbTrack where r.ssbTrack.token in (:tokens)")
    List<TrackRedisReplyDto> getRepliesByTokens(@Param("tokens") Set<String> tokens);


    Optional<SsbTrackReply> findByIdAndSsbTrack(Long id, SsbTrack ssbTrack);

    List<SsbTrackReply> findAllByParentId(Long parentId);

    @Query("select r from SsbTrackReply r join fetch r.user where r.id = :id")
    Optional<SsbTrackReply> findOne(@Param("id") Long id);

    @Query("select coalesce(max(r.replyOrder),0) from SsbTrackReply r where r.parentId =:parentId and r.ssbTrack =:ssbTrack ")
    Integer findMaxOrderByParentId(@Param("parentId") Long parentId, @Param("ssbTrack") SsbTrack ssbTrack);

    @Query(
        "select new sky.Sss.domain.track.dto.common.rep.TargetInfoDto(r.id,r.token,r.contents,r.user,r.ssbTrack.id) "
            + " from SsbTrackReply r join fetch User u "
            + " on r.user.id = u.id "
            + " where r.id = :id and r.token =:token ")
    Optional<TargetInfoDto> getTargetInfoDto(@Param("id") long id, @Param("token") String token);





//
//    @Query(" select coalesce(max(r2.replyOrder),0) from SsbTrackReply r1 join SsbTrackReply r2 "
//        + " where r2.parentId =:parentId and r2.ssbTrack =:ssbTrack ")
//    Integer findMaxOrderByParentId(@Param("parentId") Long parentId, @Param("ssbTrack") SsbTrack ssbTrack);
}

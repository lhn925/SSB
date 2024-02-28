package sky.Sss.domain.track.repository.track.reply;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.reply.SsbTrackReply;

public interface TrackReplyRepository extends JpaRepository<SsbTrackReply, Long> {


    @Query("select r from SsbTrackReply r join fetch r.ssbTrack "
        + "where r.token = :token "
        + "and r.id =:id "
        + "or r.parentId =:id ")
    List<SsbTrackReply> findListAndSubReplies(@Param("id") Long id, @Param("token") String token);

    Optional<SsbTrackReply> findByIdAndSsbTrack(Long id, SsbTrack ssbTrack);

    List<SsbTrackReply> findAllByParentId(Long parentId);

    @Query("select r from SsbTrackReply r join fetch r.user where r.id = :id")
    Optional<SsbTrackReply> findOne(@Param("id") Long id);

    @Query("select coalesce(max(r.replyOrder),0) from SsbTrackReply r where r.parentId =:parentId and r.ssbTrack =:ssbTrack ")
    Integer findMaxOrderByParentId(@Param("parentId") Long parentId, @Param("ssbTrack") SsbTrack ssbTrack);

}

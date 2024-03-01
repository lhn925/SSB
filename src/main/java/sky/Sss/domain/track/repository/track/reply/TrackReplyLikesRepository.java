package sky.Sss.domain.track.repository.track.reply;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.track.entity.track.reply.SsbTrackReply;
import sky.Sss.domain.track.entity.track.reply.SsbTrackReplyLikes;
import sky.Sss.domain.user.entity.User;

public interface TrackReplyLikesRepository extends JpaRepository<SsbTrackReplyLikes, Long> {


    Optional<SsbTrackReplyLikes> findBySsbTrackReplyIdAndUser(long replyId,User user);
    Optional<SsbTrackReplyLikes> findBySsbTrackReplyAndUser(SsbTrackReply ssbTrackReply,User user);


    boolean existsBySsbTrackReplyAndUser(SsbTrackReply ssbTrackReply, User user);


    @Query("select r from SsbTrackReplyLikes r join fetch r.user where r.ssbTrackReply.token = :token")
    List<SsbTrackReplyLikes> findAll(@Param("token") String token);

    @Query("select r.user from SsbTrackReplyLikes r join fetch User u on"
        + " r.user = u where r.ssbTrackReply.token = :token")
    List<User> getUserList(@Param("token") String token);

    @Query("select count(s.id) from SsbTrackReplyLikes s where s.ssbTrackReply.token = :replyToken")
    Integer countByReplyToken(@Param("replyToken")String replyToken);
    @Query("select count(s.id) from SsbTrackReplyLikes s where s.ssbTrackReply.id = :replyId")
    Integer countByReplyId(@Param("replyId")String replyId);

}

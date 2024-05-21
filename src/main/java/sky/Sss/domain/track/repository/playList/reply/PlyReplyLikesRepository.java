package sky.Sss.domain.track.repository.playList.reply;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.track.entity.playList.reply.SsbPlyReply;
import sky.Sss.domain.track.entity.playList.reply.SsbPlyReplyLikes;
import sky.Sss.domain.track.entity.track.reply.SsbTrackReply;
import sky.Sss.domain.track.entity.track.reply.SsbTrackReplyLikes;
import sky.Sss.domain.user.entity.User;

public interface PlyReplyLikesRepository extends JpaRepository<SsbPlyReplyLikes, Long> {


    Optional<SsbPlyReplyLikes> findBySsbPlyReplyIdAndUser(long replyId,User user);
    Optional<SsbPlyReplyLikes> findBySsbPlyReplyAndUser(SsbPlyReply ssbPlyReply,User user);


    boolean existsBySsbPlyReplyAndUser(SsbPlyReply ssbPlyReply, User user);



    @Query("select r.user from SsbPlyReplyLikes r join r.user where r.ssbPlyReply.token = :token")
    List<User> getUserList(@Param("token") String token);

    @Query("select count(s.id) from SsbPlyReplyLikes s where s.ssbPlyReply.token = :replyToken")
    Integer countByReplToken(@Param("replyToken")String replyToken);
    @Query("select count(s.id) from SsbPlyReplyLikes s where s.ssbPlyReply.id = :replyId")
    Integer countByReplyId(@Param("replyId")String replyId);

}

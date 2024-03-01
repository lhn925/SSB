package sky.Sss.domain.track.entity.playList.reply;


import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.entity.track.reply.SsbTrackReply;
import sky.Sss.domain.track.entity.track.reply.SsbTrackReplyLikes;
import sky.Sss.domain.user.entity.User;
import sky.Sss.global.base.BaseTimeEntity;


@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"uid", "reply_id"})})
@Entity
@Setter(value = PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SsbPlyReplyLikes extends BaseTimeEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 좋아요를 누른 사람
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "uid", nullable = false)
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "reply_id", nullable = false)
    private SsbPlyReply ssbPlyReply;

    public static SsbPlyReplyLikes create(User user) {
        SsbPlyReplyLikes ssbPlyReplyLikes = new SsbPlyReplyLikes();
        ssbPlyReplyLikes.setUser(user);
        return ssbPlyReplyLikes;
    }
    public static void updateReply(SsbPlyReplyLikes ssbPlyReplyLikes,long id) {
        SsbPlyReply ssbPlyReply = SsbPlyReply.builder().id(id).build();
        ssbPlyReplyLikes.setSsbPlyReply(ssbPlyReply);
    }
}

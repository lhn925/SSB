package sky.Sss.domain.track.entity.track;


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
import sky.Sss.domain.user.entity.User;
import sky.Sss.global.base.BaseTimeEntity;


@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"uid", "track_id"})})
@Entity
@Setter(value = PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SsbTrackLikes extends BaseTimeEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 좋아요를 누른 사람
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "uid", nullable = false)
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "track_id", nullable = false)
    private SsbTrack ssbTrack;


    public static SsbTrackLikes create(User user, SsbTrack ssbTrack) {
        SsbTrackLikes ssbTrackLikes = new SsbTrackLikes();
        ssbTrackLikes.setUser(user);
        ssbTrackLikes.setSsbTrack(ssbTrack);
        return ssbTrackLikes;
    }
}

package sky.Sss.domain.track.entity.playList;


import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.SsbTrackLikes;
import sky.Sss.domain.user.entity.User;
import sky.Sss.global.base.BaseTimeEntity;


@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"uid", "setting_id"})})
@Entity
@Setter(AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SsbPlyLikes extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    // 좋아요를 누른 사람
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "uid", nullable = false)
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "setting_id", nullable = false)
    private SsbPlayListSettings ssbPlayListSettings;

    public static SsbPlyLikes create(User user, SsbPlayListSettings playListSettings) {
        SsbPlyLikes ssbPlyLikes = new SsbPlyLikes();
        ssbPlyLikes.setUser(user);
        ssbPlyLikes.setSsbPlayListSettings(playListSettings);
        return ssbPlyLikes;
    }

}
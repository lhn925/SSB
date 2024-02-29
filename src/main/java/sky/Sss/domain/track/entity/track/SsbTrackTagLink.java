package sky.Sss.domain.track.entity.track;


import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.global.base.BaseTimeEntity;


@Getter
@Setter(PRIVATE)
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"track_id", "tag_id"}))
public class SsbTrackTagLink extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "track_id", nullable = false)
    private SsbTrack ssbTrack;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private SsbTrackTags ssbTrackTags;

    public static SsbTrackTagLink createSsbTrackTagLink(SsbTrack ssbTrack, SsbTrackTags ssbTrackTags) {
        SsbTrackTagLink ssbTrackTagLink = new SsbTrackTagLink();
        ssbTrackTagLink.setSsbTrackTags(ssbTrackTags);
        ssbTrackTagLink.setSsbTrack(ssbTrack);

        return ssbTrackTagLink;
    }

    public void deleteTagLink() {
        setSsbTrack(null);
    }
}

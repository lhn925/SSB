package sky.Sss.domain.track.entity.playList;


import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.SsbTrackTagLink;
import sky.Sss.domain.track.entity.track.SsbTrackTags;
import sky.Sss.global.base.BaseTimeEntity;


@Getter
@Setter(PRIVATE)
@Entity
@NoArgsConstructor(access = PROTECTED)
public class SsbPlayListTagLink extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "playlist_id")
    private SsbPlayListSettings ssbPlayListSettings;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="tag_id")
    private SsbTrackTags ssbTrackTags;

    public static SsbPlayListTagLink createSsbTrackTagLink(SsbPlayListSettings ssbPlayListSettings, SsbTrackTags ssbTrackTags) {
        SsbPlayListTagLink ssbPlayListTagLink = new SsbPlayListTagLink();
        ssbPlayListTagLink.setSsbTrackTags(ssbTrackTags);
        ssbPlayListTagLink.setSsbPlayListSettings(ssbPlayListSettings);

        return ssbPlayListTagLink;
    }
}

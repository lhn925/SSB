package sky.Sss.domain.track.entity.track;


import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.entity.track.SsbTrack;

@Entity
@Setter(AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SsbTrackTags {
    @Id
    @GeneratedValue
    private Long id;

    private String tag;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "track_id")
    private SsbTrack ssbTrack;




}

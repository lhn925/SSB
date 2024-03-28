package sky.Sss.domain.track.entity.track;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.global.base.BaseTimeEntity;

@Entity
@Setter(AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SsbTrackTags extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tag;

    public static SsbTrackTags createSsbTrackTag(String tag) {
        SsbTrackTags ssbTrackTags = new SsbTrackTags();
        ssbTrackTags.setTag(tag);
        return ssbTrackTags;
    }

    public void updateId(long id) {
        this.id = id;
    }
}

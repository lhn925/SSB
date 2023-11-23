package sky.Sss.domain.track.entity.track;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
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
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String tag;

//    @OneToMany(mappedBy = "ssbTrackTags",cascade = ALL)
//    private Set<SsbTrackTagLink> tags = new HashSet<>();
//
//    public static void addTagLink(SsbTrackTags ssbTrackTags,SsbTrackTagLink ssbTrackTagLink) {
//        ssbTrackTags.getTags().add(ssbTrackTagLink);
//    }


    public static SsbTrackTags createSsbTrackTag(String tag) {
        SsbTrackTags ssbTrackTags = new SsbTrackTags();
        ssbTrackTags.setTag(tag);
        return ssbTrackTags;
    }

}

package sky.Sss.domain.track.entity.playList;


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
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;

@Entity
@Setter(AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SsbPlayListTags {
    @Id
    @GeneratedValue
    private Long id;

    private String tag;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "setting_id")
    private SsbPlayListSettings ssbPlayListSettings;


}

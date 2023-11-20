package sky.Sss.domain.track.entity.playList;


import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.dto.TrackPlayListSettingDto;
import sky.Sss.domain.track.model.PlayListType;
import sky.Sss.domain.user.entity.User;
import sky.Sss.global.base.BaseTimeEntity;


@Getter
@Setter(AccessLevel.PRIVATE)
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SsbPlayListSettings extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "uid")
    private User user;

    // title
    @Column(nullable = false)
    private String title;

    //앨범 커버
    @Column(nullable = false)
    private String coverUrl;

    // 타입
    @Enumerated(STRING)
    @Column(nullable = false)
    private PlayListType type;

    private String token;

    // 설명
    private String description;

    // 공개여부
    private Boolean isPrivacy;

    private Boolean isDownload;

    @OneToMany(mappedBy = "ssbPlayListSettings", cascade = ALL)
    private Set<SsbPlayListTracks> likes = new HashSet<>();

    public static SsbPlayListSettings createSsbPlayListSettings(TrackPlayListSettingDto trackPlayListSettingDto,User user) {
        SsbPlayListSettings ssbPlayListSettings = new SsbPlayListSettings();
        ssbPlayListSettings.setDescription(trackPlayListSettingDto.getDesc());
        ssbPlayListSettings.setType(trackPlayListSettingDto.getPlayListType());
        ssbPlayListSettings.setIsDownload(trackPlayListSettingDto.getIsDownload());
        ssbPlayListSettings.setIsPrivacy(trackPlayListSettingDto.getIsPrivacy());
        ssbPlayListSettings.setUser(user);

        ssbPlayListSettings.setTitle(trackPlayListSettingDto.getPlayListTitle());
        return ssbPlayListSettings;
    }

    public static void updatePlayListCoverImg (String cover,SsbPlayListSettings ssbPlayListSettings) {
        ssbPlayListSettings.setCoverUrl(cover);
    }

    public static void updatePlayListToken (String token,SsbPlayListSettings ssbPlayListSettings) {
        ssbPlayListSettings.setCoverUrl(token);
    }

}

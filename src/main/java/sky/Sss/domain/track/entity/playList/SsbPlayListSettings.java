package sky.Sss.domain.track.entity.playList;


import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.dto.playlist.PlayListSettingDto;
import sky.Sss.domain.track.model.PlayListType;
import sky.Sss.domain.user.entity.User;
import sky.Sss.global.base.BaseTimeEntity;


@Getter
@Setter(PRIVATE)
@Entity
@NoArgsConstructor(access = PROTECTED)
public class SsbPlayListSettings extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "uid")
    private User user;

    // title
    // 45 자 이하 이모티콘 x
    @Column(nullable = false)
    private String title;

    //앨범 커버
    @Column(nullable = false)
    private String coverUrl;

    // 타입
    @Enumerated(STRING)
    @Column(nullable = false)
    private PlayListType playListType;

    private String token;

    // 설명 1000 자 이하
    private String description;

    // 공개여부
    private Boolean isPrivacy;

    private Boolean isDownload;

    @OneToMany(mappedBy = "ssbPlayListSettings", cascade = ALL)
    private Set<SsbPlayListLikes> likes = new HashSet<>();
    @OneToMany(mappedBy = "ssbPlayListSettings", cascade = ALL)
    private Set<SsbPlayListTagLink> tags = new HashSet<>();

    @OneToMany(mappedBy = "ssbPlayListSettings", cascade = ALL)
    private List<SsbPlayListTracks> playListTracks = new ArrayList<>();


    public static void addPlayListTracks(SsbPlayListTracks ssbPlayListTracks,SsbPlayListSettings ssbPlayListSettings) {
        ssbPlayListSettings.playListTracks.add(ssbPlayListTracks);
    }

    public static void addPlayListTagLink(SsbPlayListSettings ssbPlayListSettings, List<SsbPlayListTagLink> list) {
        list.stream().forEach(ssbTrackTagLink ->
            ssbPlayListSettings.tags.add(ssbTrackTagLink)
        );
    }

    public static SsbPlayListSettings createSsbPlayListSettings(PlayListSettingDto playListSettingDto,
        User user) {
        SsbPlayListSettings ssbPlayListSettings = new SsbPlayListSettings();
        ssbPlayListSettings.setDescription(playListSettingDto.getDesc());
        ssbPlayListSettings.setPlayListType(playListSettingDto.getPlayListType());
        ssbPlayListSettings.setIsDownload(playListSettingDto.isDownload());
        ssbPlayListSettings.setIsPrivacy(playListSettingDto.isDownload());
        ssbPlayListSettings.setUser(user);

        ssbPlayListSettings.setTitle(playListSettingDto.getTitle());
        return ssbPlayListSettings;
    }

    public static void updatePlayListCoverImg(String coverUrl, SsbPlayListSettings ssbPlayListSettings) {
        ssbPlayListSettings.setCoverUrl(coverUrl);
    }

    public static void updatePlayListToken(String token, SsbPlayListSettings ssbPlayListSettings) {
        ssbPlayListSettings.setToken(token);
    }

}

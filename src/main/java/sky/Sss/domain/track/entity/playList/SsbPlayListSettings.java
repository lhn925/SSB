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
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import sky.Sss.domain.track.dto.playlist.PlayListSettingSaveDto;
import sky.Sss.domain.track.entity.playList.reply.SsbPlyReply;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.model.PlayListType;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Status;
import sky.Sss.global.base.BaseTimeEntity;
import sky.Sss.global.file.utili.FileStore;
import sky.Sss.global.utili.JsEscape;


@Slf4j
@Getter
@Setter(PRIVATE)
@Entity
@NoArgsConstructor(access = PROTECTED)
public class SsbPlayListSettings extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "uid")
    private User user;

    // title
    // 45 자 이하 이모티콘 x
    @Column(nullable = false)
    private String title;

    //앨범 커버
    private String coverUrl;

    // 타입
    @Enumerated(STRING)
    @Column(nullable = false)
    private PlayListType playListType;

    @Column(nullable = false)
    private String token;

    // 설명 1000 자 이하
    private String description;

    // 공개여부
    @Column(nullable = false)
    private Boolean isPrivacy;

    @Column(nullable = false)
    private Boolean isDownload;

    @Column(nullable = false)
    private Boolean isStatus;

    // feed 에 공개가 된적 이 있는지
    // true 면 feed 에 공개 됨, false 면 공개 된 적 없음
    @Column(nullable = false)
    private Boolean isRelease;

    @OneToMany(mappedBy = "ssbPlayListSettings", cascade = ALL)
    private List<SsbPlyLikes> likes = new ArrayList<>();
    @OneToMany(mappedBy = "ssbPlayListSettings", cascade = ALL)
    private List<SsbPlayListTagLink> tags = new ArrayList<>();

    @OneToMany(mappedBy = "ssbPlayListSettings", cascade = ALL)
    private List<SsbPlayListTracks> playListTracks = new ArrayList<>();
    @OneToMany(mappedBy = "ssbPlayListSettings", cascade = ALL)
    private List<SsbPlyReply> replies = new ArrayList<>();
    public static SsbPlayListSettings create(PlayListSettingSaveDto playListSettingSaveDto,
        User user) {
        SsbPlayListSettings ssbPlayListSettings = new SsbPlayListSettings();
        if (playListSettingSaveDto.getDesc().trim().length() > 1000) {
            throw new IllegalArgumentException("desc.error.length");
        }
        ssbPlayListSettings.setTitle(JsEscape.escapeJS(playListSettingSaveDto.getTitle()));

        ssbPlayListSettings.setDescription(JsEscape.escapeJS(playListSettingSaveDto.getDesc()));

        PlayListType playListType = PlayListType.findByListType(playListSettingSaveDto.getPlayListType());
        ssbPlayListSettings.setPlayListType(playListType);

        ssbPlayListSettings.setIsDownload(playListSettingSaveDto.isDownload());
        ssbPlayListSettings.setIsPrivacy(playListSettingSaveDto.isPrivacy());
        ssbPlayListSettings.setUser(user);
        changeStatus(ssbPlayListSettings, Status.ON);
        return ssbPlayListSettings;
    }

    public static void updateIsRelease(SsbPlayListSettings ssbPlayListSettings,Boolean isRelease) {
        ssbPlayListSettings.setIsRelease(isRelease);
    }
    public static void updateInfo(SsbPlayListSettings ssbPlayListSettings, String title, String desc,
        String playListType, Boolean isDownload, Boolean isPrivacy) {
        PlayListType type = PlayListType.findByListType(playListType);
        ssbPlayListSettings.setIsPrivacy(isPrivacy);
        ssbPlayListSettings.setIsDownload(isDownload);
        ssbPlayListSettings.setPlayListType(type);
        ssbPlayListSettings.setTitle(JsEscape.escapeJS(title));
        if (desc.trim().length() > 1000) {
            throw new IllegalArgumentException("desc.error.length");
        }
    }

    public static void changeStatus(SsbPlayListSettings ssbPlayListSettings, Status isStatus) {
        ssbPlayListSettings.setIsStatus(isStatus.getValue());
    }

    public static void updateCoverImg(String coverUrl, SsbPlayListSettings ssbPlayListSettings) {
        ssbPlayListSettings.setCoverUrl(coverUrl);
    }

    public static void deleteCoverImg(FileStore fileStore, SsbPlayListSettings ssbPlayListSettings) {
        if (StringUtils.hasText(ssbPlayListSettings.getCoverUrl())) {
            fileStore.deleteFile(FileStore.IMAGE_DIR,
                ssbPlayListSettings.getCoverUrl());
        }
    }

    public static void updateToken(String token, SsbPlayListSettings ssbPlayListSettings) {
        ssbPlayListSettings.setToken(token);
    }

    @Builder
    public SsbPlayListSettings(long id ){
        this.id = id;
    }

}

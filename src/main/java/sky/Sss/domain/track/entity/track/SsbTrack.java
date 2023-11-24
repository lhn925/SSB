package sky.Sss.domain.track.entity.track;


import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;
import sky.Sss.domain.track.dto.track.TrackInfoSaveDto;
import sky.Sss.domain.track.entity.TempTrackStorage;
import sky.Sss.domain.track.entity.playList.SsbPlayListTracks;
import sky.Sss.domain.track.model.TrackGenre;
import sky.Sss.domain.user.entity.User;
import sky.Sss.global.base.BaseTimeEntity;
import sky.Sss.global.file.utili.FileStore;

@Entity
@Setter(AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SsbTrack extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "uid")
    private User user;

    @Enumerated(STRING)
    private TrackGenre genreType;

    private String genre;

    // 다운로드 허용 여부 ture 허용, false 불가
    @Column(nullable = false)
    private Boolean isDownload;

    private String description;

    // track 커버
    private String coverUrl;

    // 공개 여부 True면 공개 false면 비공개
    @Column(nullable = false)
    private Boolean isPrivacy;

    @Column(nullable = false)
    private Long size;
    @Column(nullable = false)
    private Integer trackLength;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private String originalName;
    @Column(nullable = false)
    private String storeFileName;

    @OneToMany(mappedBy = "ssbTrack", cascade = ALL)
    private Set<SsbTrackTagLink> tags = new HashSet<>();
    @OneToMany(mappedBy = "ssbTrack", cascade = ALL)
    private Set<SsbTrackLikes> likes = new HashSet<>();
    @OneToMany(mappedBy = "ssbTrack", cascade = ALL)
    private Set<SsbTrackViews> views = new HashSet<>();

    // 비활성화 여부 true:삭제 ,false:삭제x
    @Column(nullable = false)
    private Boolean isEnabled;

    public static void addTagLink(SsbTrack ssbTrack, List<SsbTrackTagLink> list) {
        if (list.size() > 0) {
            list.stream().forEach(ssbTrackTagLink ->
                ssbTrack.tags.add(ssbTrackTagLink)
            );
        } else {
            // 아무것도 없으면 전부 삭제
            removeTagLink(ssbTrack);
        }
    }

    public static void removeTagLink(SsbTrack ssbTrack) {
        ssbTrack.tags.clear();
    }

    public static SsbTrack createSsbTrack(TrackInfoSaveDto trackInfoSaveDto, TempTrackStorage tempTrackStorage,
        User user) {
        SsbTrack ssbTrack = new SsbTrack();
        setUploadTrackFile(tempTrackStorage, ssbTrack);
        uploadTrackInfo(ssbTrack, trackInfoSaveDto.getGenre(), trackInfoSaveDto.getGenreType(),
            trackInfoSaveDto.isPrivacy(), trackInfoSaveDto.isDownload(), trackInfoSaveDto.getTitle(),
            trackInfoSaveDto.getDesc());
        ssbTrack.setUser(user);
        ssbTrack.setIsEnabled(false);
        return ssbTrack;
    }

    public static void uploadTrackInfo(SsbTrack ssbTrack, String genre, TrackGenre trackGenre, Boolean isPrivacy,
        Boolean isDownload, String title, String description) {
        ssbTrack.setGenre(genre);
        ssbTrack.setGenreType(trackGenre);
        ssbTrack.setIsPrivacy(isPrivacy);
        ssbTrack.setIsDownload(isDownload);
        ssbTrack.setTitle(title);
        ssbTrack.setDescription(description);
    }

    public static void updateToken(String token, SsbTrack ssbTrack) {
        ssbTrack.setToken(token);
    }

    public static void changeEnabled(SsbTrack ssbTrack, Boolean isEnabled) {
        ssbTrack.setIsEnabled(isEnabled);
    }

    //파일 정보 저장
    private static void setUploadTrackFile(TempTrackStorage tempTrackStorage, SsbTrack ssbTrack) {
        ssbTrack.setTrackLength(tempTrackStorage.getTrackLength());
        ssbTrack.setSize(tempTrackStorage.getSize());
        ssbTrack.setOriginalName(tempTrackStorage.getOriginalName());
        ssbTrack.setStoreFileName(tempTrackStorage.getStoreFileName());
    }


    public static void deleteSsbTrack(SsbTrack ssbTrack, FileStore fileStore) throws IOException {
        if (StringUtils.hasText(ssbTrack.getStoreFileName())) {
            fileStore.deleteFile(FileStore.TRACK_DIR, ssbTrack.getToken(), ssbTrack.getStoreFileName());
        }
    }

    public static void deleteSsbTrackCover(SsbTrack ssbTrack, FileStore fileStore) throws IOException {
        if (StringUtils.hasText(ssbTrack.getCoverUrl())) {
            fileStore.deleteFile(FileStore.TRACK_COVER_DIR, ssbTrack.getToken(), ssbTrack.getCoverUrl());
        }
    }

    public static void updateTrackCoverImg(String coverUrl, SsbTrack ssbTrack) {
        ssbTrack.setCoverUrl(coverUrl);
    }

    public static String getSsbTrackCoverPath(FileStore fileStore, String token) {
        return fileStore.getTrackCoverDir() + token + "/";
    }

}

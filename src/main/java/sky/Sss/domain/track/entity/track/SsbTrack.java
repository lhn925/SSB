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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;
import sky.Sss.domain.track.dto.TrackMetaUploadDto;
import sky.Sss.domain.track.entity.TempTrackStorage;
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


    public static void addTagLink(SsbTrack ssbTrack, List<SsbTrackTagLink> list) {
        list.stream().forEach(ssbTrackTagLink ->
            ssbTrack.tags.add(ssbTrackTagLink)
        );
    }

    public static SsbTrack createSsbTrack(TrackMetaUploadDto trackMetaUploadDto, TempTrackStorage tempTrackStorage,
        User user) {
        SsbTrack ssbTrack = new SsbTrack();
        setUploadTrackFile(tempTrackStorage, ssbTrack);
        setTrackInfo(ssbTrack, trackMetaUploadDto.getGenre(), trackMetaUploadDto.getGenreType(),
            trackMetaUploadDto.isPrivacy(), trackMetaUploadDto.isDownload(), trackMetaUploadDto.getTitle());
        ssbTrack.setUser(user);
        return ssbTrack;
    }

    private static void setTrackInfo(SsbTrack ssbTrack, String genre, TrackGenre trackGenre, Boolean isPrivacy,
        Boolean isDownload, String title) {
        ssbTrack.setGenre(genre);
        ssbTrack.setGenreType(trackGenre);
        ssbTrack.setIsPrivacy(isPrivacy);
        ssbTrack.setIsDownload(isDownload);
        ssbTrack.setTitle(title);
    }

    public static void updateToken(String token, SsbTrack ssbTrack) {
        ssbTrack.setToken(token);
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
            fileStore.deleteFile(getSsbTrackPath(fileStore, ssbTrack.getToken()), ssbTrack.getStoreFileName());
        }
    }

    public static void deleteSsbTrackCover(SsbTrack ssbTrack, FileStore fileStore) throws IOException {
        if (StringUtils.hasText(ssbTrack.getStoreFileName())) {
            fileStore.deleteFile(getSsbTrackCoverPath(fileStore, ssbTrack.getToken()), ssbTrack.getStoreFileName());
        }
    }

    public static void updateTrackCoverImg(String coverUrl, SsbTrack ssbTrack) {
        ssbTrack.setCoverUrl(coverUrl);
    }


    public static String getSsbTrackPath(FileStore fileStore, String token) {
        return fileStore.getFileDir() + fileStore.getTrackFileDir() + token + "/";
    }

    public static String getSsbTrackCoverPath(FileStore fileStore, String token) {
        return fileStore.getFileDir() + fileStore.getTrackCoverDir() + token + "/";
    }

}

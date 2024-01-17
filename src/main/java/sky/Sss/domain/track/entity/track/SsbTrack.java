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
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import sky.Sss.domain.track.dto.track.TrackInfoSaveDto;
import sky.Sss.domain.track.entity.TempTrackStorage;
import sky.Sss.domain.track.model.MainGenreType;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Status;
import sky.Sss.global.base.BaseTimeEntity;
import sky.Sss.global.file.utili.FileStore;
import sky.Sss.global.utili.JSEscape;

@Slf4j
@Entity
@Setter(AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SsbTrack extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    // 45 자 이하 이모티콘 x
    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "uid")
    private User user;

    @Enumerated(STRING)
    private MainGenreType mainGenreType;

    // 이모티콘 사용 불가능
    private String genre;

    // 다운로드 허용 여부 ture 허용, false 불가
    @Column(nullable = false)
    private Boolean isDownload;

    // 1000자 이하
    private String description;

    // track 커버
    private String coverUrl;

    // 공개 여부 True 면 비공개 false 면 공개
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

    // 비활성화 여부 true:활성화 ,false:비활성화x
    @Column(nullable = false)
    private Boolean isStatus;

    @OneToMany(mappedBy = "ssbTrack", cascade = ALL)
    private List<SsbTrackTagLink> tags = new ArrayList<>();

    @OneToMany(mappedBy = "ssbTrack", cascade = ALL)
    private List<SsbTrackLikes> likes = new ArrayList<>();

    @OneToMany(mappedBy = "ssbTrack", cascade = ALL)
    private List<SsbTrackAllPlayLogs> plays = new ArrayList<>();


    public static void addTagLink(SsbTrack ssbTrack, List<SsbTrackTagLink> tagLinks) {
        if (tagLinks != null && tagLinks.size() > 0) {
            tagLinks.stream().forEach(ssbTrackTagLink ->
                ssbTrack.tags.add(ssbTrackTagLink)
            );
        } else {
            // 아무것도 없으면 전부 삭제
            rmTagLink(ssbTrack);
        }
    }
    // 링크 삭제
    public static void rmTagLink(SsbTrack ssbTrack, SsbTrackTagLink tagLinks) {
        ssbTrack.getTags().remove(tagLinks);
    }

    public static void rmTagLink(SsbTrack ssbTrack) {
        ssbTrack.tags.clear();
    }

    public static SsbTrack create(TrackInfoSaveDto trackInfoSaveDto, TempTrackStorage tempTrackStorage,
        User user) {
        SsbTrack ssbTrack = new SsbTrack();
        setUploadTrackFile(tempTrackStorage, ssbTrack);
        uploadInfo(ssbTrack, trackInfoSaveDto.getGenre(), trackInfoSaveDto.getGenreType(),
            trackInfoSaveDto.getIsPrivacy(), trackInfoSaveDto.getIsDownload(), trackInfoSaveDto.getTitle(),
            trackInfoSaveDto.getDesc());
        ssbTrack.setUser(user);

        SsbTrack.changeStatus(ssbTrack, Status.ON);
        return ssbTrack;
    }

    public static void uploadInfo(SsbTrack ssbTrack, String genre, String mainGenreType, Boolean isPrivacy,
        Boolean isDownload, String title, String description) {
        // enum Type 적용
        MainGenreType type = MainGenreType.findByType(mainGenreType);
        String subGenreType = type.getSubGenreValue(genre);
        ssbTrack.setTitle(JSEscape.escapeJS(title));

        ssbTrack.setGenre(subGenreType);

        ssbTrack.setMainGenreType(type);
        ssbTrack.setIsPrivacy(isPrivacy);
        ssbTrack.setIsDownload(isDownload);
        if (description.trim().length() > 1000) {
            throw new IllegalArgumentException("track.desc.error.length");
        }

        ssbTrack.setDescription(JSEscape.escapeJS(description));
    }

    public static void updateToken(String token, SsbTrack ssbTrack) {
        ssbTrack.setToken(token);
    }

    public static void changeStatus(SsbTrack ssbTrack, Status isStatus) {
        ssbTrack.setIsStatus(isStatus.getValue());
    }

    //파일 정보 저장
    private static void setUploadTrackFile(TempTrackStorage tempTrackStorage, SsbTrack ssbTrack) {
        ssbTrack.setTrackLength(tempTrackStorage.getTrackLength());
        ssbTrack.setSize(tempTrackStorage.getSize());
        ssbTrack.setOriginalName(tempTrackStorage.getOriginalName());
        ssbTrack.setStoreFileName(tempTrackStorage.getStoreFileName());

        tempTrackStorage.getCreatedDateTime();
    }


    public static void deleteTrackFile(SsbTrack ssbTrack, FileStore fileStore) {
        if (StringUtils.hasText(ssbTrack.getStoreFileName())) {
            fileStore.deleteFile(FileStore.TRACK_DIR, ssbTrack.getStoreFileName());
        }
    }

    public static void deleteCoverImg(SsbTrack ssbTrack, FileStore fileStore) {
        if (StringUtils.hasText(ssbTrack.getCoverUrl())) {
            fileStore.deleteFile(FileStore.IMAGE_DIR, ssbTrack.getCoverUrl());
        }
    }

    public static void updateCoverImg(String coverUrl, SsbTrack ssbTrack) {
        if (StringUtils.hasText(coverUrl)) {
            ssbTrack.setCoverUrl(coverUrl);
        }

    }

    public static String getSsbTrackCoverPath(FileStore fileStore, String token) {
        return fileStore.getImageDir() + token + "/";
    }

}

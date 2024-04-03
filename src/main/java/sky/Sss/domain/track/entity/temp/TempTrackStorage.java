package sky.Sss.domain.track.entity.temp;


import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;
import sky.Sss.domain.track.exception.checked.SsbFileNotFoundException;
import sky.Sss.domain.user.entity.User;
import sky.Sss.global.base.BaseTimeEntity;
import sky.Sss.global.file.dto.UploadTrackFileDto;
import sky.Sss.global.file.utili.FileStore;

@Entity
@Getter
@Setter(value = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class TempTrackStorage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token;

    @ManyToOne
    @JoinColumn(name = "uid")
    private User user;

    @Column(nullable = false)
    private Long size;
    @Column(nullable = false)
    private Integer trackLength;
    @Column(nullable = false)
    private String originalName;
    @Column(nullable = false)
    private String storeFileName;
    @Column(nullable = false)
    private Boolean isPlayList;

    @Column(nullable = false)
    private Boolean isPrivacy; // ture: 비공개 , false 공개
    // true playList , false : track


    public static TempTrackStorage createTempTrackStorage(UploadTrackFileDto uploadTrackFileDto, String token,
        User user, Boolean isPlayList, boolean isPrivacy) {
        TempTrackStorage tempTrackStorage = new TempTrackStorage();
        tempTrackStorage.setSize(uploadTrackFileDto.getSize());
        tempTrackStorage.setTrackLength(uploadTrackFileDto.getTrackLength());
        tempTrackStorage.setUser(user);
        tempTrackStorage.setToken(token);
        tempTrackStorage.setIsPlayList(isPlayList);
        tempTrackStorage.setStoreFileName(uploadTrackFileDto.getStoreFileName());
        tempTrackStorage.setOriginalName(uploadTrackFileDto.getOriginalFileName());
        tempTrackStorage.setIsPrivacy(isPrivacy);
        return tempTrackStorage;
    }

    public static void deleteTempFile(TempTrackStorage tempTrackStorage, FileStore fileStore)
        throws SsbFileNotFoundException {
        if (StringUtils.hasText(tempTrackStorage.getStoreFileName())) {
            fileStore.deleteFile(FileStore.TRACK_DIR,
                tempTrackStorage.getToken() + "/" + tempTrackStorage.getStoreFileName());
        }
    }

}

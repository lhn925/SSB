package sky.Sss.domain.track.dto;


import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import sky.Sss.domain.track.model.PlayListType;

@Getter
@Setter
public class TrackPlayListSettingDto {
    // 플레이리스트 제목
    private String playListTitle;
    // 플레이리스트 타입
    private PlayListType playListType;

    private MultipartFile coverImgFile;
    private String desc;
    private Boolean isDownload;
    private Boolean isPrivacy;

    private List<TrackPlayListFileDto> trackPlayListFileDtoList;

}

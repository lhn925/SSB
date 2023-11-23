package sky.Sss.domain.track.dto.track;


import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import sky.Sss.domain.track.model.PlayListType;
import sky.Sss.global.utili.annotation.MultipartPictureValid;

@Getter
@Setter
public class TrackPlayListSettingDto {
    // 플레이리스트 제목
    @NotBlank
    private String playListTitle;
    // 플레이리스트 타입
    @NotBlank
    private PlayListType playListType;

    private String desc;

    @NotBlank
    private Boolean isDownload;
    @NotBlank
    private Boolean isPrivacy;

    private Set<TrackTagsDto> tagSet;

    private List<TrackPlayListMetaDto> trackPlayListMetaDto;

    @MultipartPictureValid
    private MultipartFile coverImgFile;
}

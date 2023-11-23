package sky.Sss.domain.track.dto;


import jakarta.validation.constraints.NotBlank;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import sky.Sss.domain.track.model.TrackGenre;
import sky.Sss.global.utili.annotation.MultipartPictureValid;

@Getter
@Setter
public class TrackMetaUploadDto {

    @NotBlank
    private String title;
    @NotBlank
    private TrackGenre genreType;

    private Long id;

    private String token;

    private Long trackLength;

    private String genre;

    private String desc;

    private boolean isDownload;

    private boolean isPrivacy;

    @MultipartPictureValid
    private MultipartFile coverImgFile;

    private Set<TrackTagsDto> tagSet;


}

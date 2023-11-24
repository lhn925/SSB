package sky.Sss.domain.track.dto.track;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import sky.Sss.domain.track.dto.tag.TrackTagsDto;
import sky.Sss.domain.track.model.TrackGenre;
import sky.Sss.global.utili.annotation.MultipartPictureValid;

@Getter
@Setter
public class TrackInfoUpdateDto {
    @NotBlank
    private String title;
    @NotNull
    private TrackGenre genreType;

    @NotNull
    private Long id;

    @NotBlank
    private String token;

    private String genre;

    private String desc;

    private boolean isDownload;

    private boolean isPrivacy;

    @MultipartPictureValid
    private MultipartFile coverImgFile;

    private Set<TrackTagsDto> tagSet;
}

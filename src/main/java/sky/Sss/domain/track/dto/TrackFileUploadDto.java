package sky.Sss.domain.track.dto;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import sky.Sss.domain.track.model.TrackGenre;

@Getter
@Setter
public class TrackFileUploadDto {

    private String title;

    private TrackGenre genreType;

    private String genre;

    private String desc;

    private Boolean isDownload;

    private Boolean isPrivacy;

    private String tags;

    private MultipartFile trackFile;

    private MultipartFile coverImgFile;
}

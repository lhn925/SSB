package sky.Sss.domain.track.dto.temp;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import sky.Sss.global.utili.validation.annotation.MultipartTrackFileValid;

@Getter
@Setter
public class TempTrackFileUploadDto {

    @MultipartTrackFileValid
    private MultipartFile trackFile;
    private boolean isPlayList;
    private boolean isPrivacy;
}

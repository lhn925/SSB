package sky.Sss.domain.track.dto;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import sky.Sss.global.utili.annotation.MultipartTrackFileValid;

@Getter
@Setter
public class TrackTempFileUploadDto {

    @MultipartTrackFileValid
    private MultipartFile trackFile;
    private boolean isPlayList;
}

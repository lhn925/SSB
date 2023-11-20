package sky.Sss.domain.track.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class TrackPlayListFileDto extends TrackFileUploadDto {


    // 순서
    private Integer order;
}

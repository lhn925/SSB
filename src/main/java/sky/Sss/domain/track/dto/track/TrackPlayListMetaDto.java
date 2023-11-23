package sky.Sss.domain.track.dto.track;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.domain.track.dto.track.TrackMetaUploadDto;

@Getter
@Setter
public class TrackPlayListMetaDto extends TrackMetaUploadDto {


    // 순서
    @NotBlank
    private Integer order;
}

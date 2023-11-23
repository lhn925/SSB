package sky.Sss.domain.track.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrackPlayListMetaDto extends TrackMetaUploadDto {


    // 순서
    @NotBlank
    private Integer order;
}

package sky.Sss.domain.track.dto.track;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.domain.track.dto.BaseTrackDto;

@Getter
@Setter
public class TrackInfoSaveReqDto extends BaseTrackDto {

    @NotBlank
    private String genreType;
    private Long trackLength;
    @NotBlank
    private String genre;
}

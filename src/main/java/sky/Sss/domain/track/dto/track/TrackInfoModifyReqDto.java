package sky.Sss.domain.track.dto.track;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.domain.track.dto.BaseTrackDto;

@Getter
@Setter
public class TrackInfoModifyReqDto extends BaseTrackDto {

    @NotBlank
    private String genreType;
    @NotBlank
    private String genre;

}

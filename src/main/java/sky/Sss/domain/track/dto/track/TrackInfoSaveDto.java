package sky.Sss.domain.track.dto.track;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.domain.track.dto.BaseTrackDto;
import sky.Sss.domain.track.model.MainGenreType;

@Getter
@Setter
public class TrackInfoSaveDto extends BaseTrackDto {

    @NotBlank
    private String genreType;
    private Long trackLength;

    @NotBlank
    private String genre;

}

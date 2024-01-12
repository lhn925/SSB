package sky.Sss.domain.track.dto.track;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.dto.BaseTrackDto;
import sky.Sss.domain.track.model.MainGenreType;

@Getter
@Setter
@NoArgsConstructor
public class TrackInfoUpdateDto extends BaseTrackDto {

    @NotBlank
    private String genreType;
    @NotBlank
    private String genre;

}

package sky.Sss.domain.track.dto.track;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.domain.track.dto.BaseTrackDto;
import sky.Sss.global.utili.validation.regex.RegexPatterns;

@Getter
@Setter
public class TrackInfoModifyReqDto extends BaseTrackDto {

    @NotBlank
    private String genreType;
    @Pattern(regexp = RegexPatterns.GENRE_REGEX, message = "track.genre.regex")
    private String genre;

}

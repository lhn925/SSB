package sky.Sss.domain.track.dto.tag;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.global.utili.validation.regex.RegexPatterns;

@Getter
@Setter
public class TrackTagsDto {
    @NotNull
    private Long id;
    @Pattern(regexp = RegexPatterns.TAG_REGEX)
    private String tag;

}

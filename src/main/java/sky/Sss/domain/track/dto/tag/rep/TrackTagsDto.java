package sky.Sss.domain.track.dto.tag.rep;


import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.global.utili.validation.regex.RegexPatterns;

@Getter
@Setter
@NoArgsConstructor
public class TrackTagsDto {
    private Long id;

    private String tag;

    public TrackTagsDto(Long id, String tag) {
        this.id = id;
        this.tag = tag;
    }
}

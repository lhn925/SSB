package sky.Sss.domain.track.dto.track.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.domain.track.dto.tag.rep.TrackTagsDto;
import sky.Sss.global.utili.validation.regex.RegexPatterns;

@Getter
@Setter
public class BaseTrackDto {
    @NotNull
    private Long id;

    @NotBlank
    private String token;
    @Pattern(regexp = RegexPatterns.TRACK_TITLE_REGEX, message = "{track.title.regex}")
    protected String title;

    @Pattern(regexp = RegexPatterns.TRACK_DESC_REGEX, message = "{desc.error.length}")
    protected String desc;

    protected boolean isDownload;
    protected boolean isPrivacy;
    @NotNull
    protected List<TrackTagsDto> tagList;

}

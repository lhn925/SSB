package sky.Sss.domain.track.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.domain.track.dto.tag.TrackTagsDto;
import sky.Sss.global.utili.validation.regex.RegexPatterns;

@Getter
@Setter
public class BaseTrackDto {
    @NotNull
    private Long id;

    @NotBlank
    private String token;
    @Pattern(regexp = RegexPatterns.TRACK_TITLE_REGEX, message = "track.title.regex")
    protected String title;

    @NotNull
    protected String desc;

    @NotNull
    protected Boolean isDownload;
    @NotNull
    protected Boolean isPrivacy;
    protected List<TrackTagsDto> tagList;

}
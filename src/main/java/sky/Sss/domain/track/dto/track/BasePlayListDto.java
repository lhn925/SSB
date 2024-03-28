package sky.Sss.domain.track.dto.track;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.domain.track.dto.tag.TrackTagsDto;
import sky.Sss.global.utili.validation.regex.RegexPatterns;

@Getter
@Setter
public class BasePlayListDto {
    @Pattern(regexp = RegexPatterns.TRACK_TITLE_REGEX, message = "track.title.regex")
    protected String title;
    @NotNull
    protected String desc;
    protected boolean isDownload;
    protected boolean isPrivacy;
    protected List<TrackTagsDto> tagList;
    // 플레이리스트 타입
    @NotBlank
    private String playListType;
}

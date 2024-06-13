package sky.Sss.domain.track.dto.common.repost;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.Set;
import lombok.Getter;
import sky.Sss.global.utili.validation.regex.RegexPatterns;

@Getter
public class RepostModifyReqDto {



    @NotBlank
    private String repostToken;
    @Min(value = 1)
    private Long repostId;

    @NotBlank
    @Pattern(regexp = RegexPatterns.REPOST_COMMENT_REGEX)
    private String comment;
    private Set<String> userTagSet;
}

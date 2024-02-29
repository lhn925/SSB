package sky.Sss.domain.track.dto.playlist.reply;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.Set;
import lombok.Getter;
import sky.Sss.global.utili.validation.regex.RegexPatterns;

@Getter
public class PlyReplySaveReqDto {


    @Min(value = 1)
    private long plyId;

    @NotBlank
    private String plyToken;

    @Min(value = 0)
    private long parentId;

    private Set<String> userTagSet;

    // 댓글은 1000자를 넘길수 없습니다.
    //Comment must not exceed 1000 characters
    @NotBlank
    @Pattern(regexp = RegexPatterns.REPLY_CONTENTS_REGEX)
    private String contents;
}

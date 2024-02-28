package sky.Sss.domain.track.dto.track.reply;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.global.utili.validation.regex.RegexPatterns;

@Getter
public class TrackReplyRmReqDto {

    @Min(value = 1)
    private long trackId;

    private String trackToken;

    private long replyId;
    @NotBlank
    private String replyToken;
}

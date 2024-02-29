package sky.Sss.domain.track.dto.track.reply;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReplyRmReqDto {

    @Min(value = 1)
    private long id;

    private String token;

    private long replyId;
    @NotBlank
    private String replyToken;
}

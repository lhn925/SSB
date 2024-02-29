package sky.Sss.domain.track.dto.playlist.reply;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PlyReplyRmReqDto {

    @Min(value = 1)
    private long plyId;

    @NotBlank
    private String plyToken;

    private long replyId;
    @NotBlank
    private String replyToken;
}

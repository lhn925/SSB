package sky.Sss.domain.track.dto.track.reply;


import jakarta.validation.constraints.Min;
import lombok.Getter;
import sky.Sss.domain.track.dto.common.reply.ReplySaveReqDto;

@Getter
public class TrackReplySaveReqDto extends ReplySaveReqDto {
    @Min(value = 0)
    private int timeLine;
}

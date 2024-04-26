package sky.Sss.domain.track.dto.track.reply;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TracksInfoReqDto {

    @NotNull
    private List<Long> ids;
}

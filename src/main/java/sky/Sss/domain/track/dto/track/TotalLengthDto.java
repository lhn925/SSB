package sky.Sss.domain.track.dto.track;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotalLengthDto {

    private Integer totalLength;
    private Long limit;

    public TotalLengthDto(Integer totalLength,Long limit) {
        this.totalLength = totalLength;
        this.limit = limit;
    }
}

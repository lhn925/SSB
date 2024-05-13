package sky.Sss.domain.track.dto.track.rep;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotalLengthRepDto {

    private Integer totalLength;
    private Long limit;

    public TotalLengthRepDto(Integer totalLength,Long limit) {
        this.totalLength = totalLength;
        this.limit = limit;
    }
}

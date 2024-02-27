package sky.Sss.domain.track.dto.track;


import static lombok.AccessLevel.PROTECTED;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class TotalCountDto implements Serializable {
    private Integer totalCount;
}

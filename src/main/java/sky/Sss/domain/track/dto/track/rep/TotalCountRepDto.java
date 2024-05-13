package sky.Sss.domain.track.dto.track.rep;


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
public class TotalCountRepDto implements Serializable {
    private Integer totalCount;
}

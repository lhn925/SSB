package sky.Sss.domain.track.dto.common.rep;

import static lombok.AccessLevel.PROTECTED;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class BaseDetailDto {

    private Integer likeCount;
    private Integer replyCount;
    private Integer repostCount;

    public BaseDetailDto(Integer likeCount, Integer replyCount, Integer repostCount) {
        this.likeCount = likeCount;
        this.replyCount = replyCount;
        this.repostCount = repostCount;
    }

}

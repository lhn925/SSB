package sky.Sss.domain.track.dto.common.rep;

import static lombok.AccessLevel.PROTECTED;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class BaseSearchInfoDto {

    private Integer likeCount;
    private Integer replyCount;
    private Integer repostCount;

    public BaseSearchInfoDto(Integer likeCount, Integer replyCount, Integer repostCount) {
        this.likeCount = likeCount;
        this.replyCount = replyCount;
        this.repostCount = repostCount;
    }

}

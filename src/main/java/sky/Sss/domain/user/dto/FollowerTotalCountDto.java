package sky.Sss.domain.user.dto;


import static lombok.AccessLevel.PROTECTED;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor(access = PROTECTED)
@Getter
@Setter(value = AccessLevel.PRIVATE)
public class FollowerTotalCountDto {
    private Integer totalCount;

    public FollowerTotalCountDto(Integer followerTotalCount) {
        this.totalCount = followerTotalCount;
    }
}

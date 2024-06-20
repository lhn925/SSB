package sky.Sss.domain.user.dto.follows;


import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.domain.user.dto.redis.RedisFollowsDto;

@Getter
@Setter(value = AccessLevel.PRIVATE)
public class FollowsUserListDto {


    private List<RedisFollowsDto> followsInfoList;
    private Integer totalCount;


    public FollowsUserListDto(List<RedisFollowsDto> followsInfoList, Integer totalCount) {
        this.followsInfoList = followsInfoList;
        this.totalCount = totalCount;
    }
}

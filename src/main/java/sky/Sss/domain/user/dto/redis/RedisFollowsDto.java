package sky.Sss.domain.user.dto.redis;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.user.entity.UserFollows;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class RedisFollowsDto {
    private Long id;

    private Long followerUid;

    private Long followingUid;

    private LocalDateTime createdDateTime;
    @Builder
    public RedisFollowsDto(Long id, Long followerUid, Long followingUid,LocalDateTime createdDateTime) {
        this.id = id;
        this.followerUid = followerUid;
        this.followingUid = followingUid;
        this.createdDateTime = createdDateTime;
    }

    public static RedisFollowsDto create(UserFollows userFollows) {
        return RedisFollowsDto.builder().followerUid(userFollows.getFollowerUser().getId())
            .followingUid(userFollows.getFollowingUser().getId())
            .id(userFollows.getId())
            .createdDateTime(userFollows.getCreatedDateTime()).build();
    }

}

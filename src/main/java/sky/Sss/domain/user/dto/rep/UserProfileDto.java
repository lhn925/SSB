package sky.Sss.domain.user.dto.rep;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(value = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfileDto {
    private Long id;
    private String userName;
    private Integer followerCount;
    private Integer trackTotalCount;
    private Integer followingCount;
    private String pictureUrl;

    @Builder
    public UserProfileDto(Long id, String userName,
        int followerCount,
        int followingCount,
        int trackTotalCount,
        String pictureUrl) {
        this.id = id;
        this.userName = userName;
        this.followerCount = followerCount;
        this.trackTotalCount = trackTotalCount;
        this.followingCount = followingCount;
        this.pictureUrl = pictureUrl;
    }
}

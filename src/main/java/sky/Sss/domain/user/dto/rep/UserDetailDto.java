package sky.Sss.domain.user.dto.rep;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(value = AccessLevel.PRIVATE)
public class UserDetailDto {
    private Long uid;
    private Long userName;
    private Long followerCount;
    private Long trackTotalCount;
    private String pictureUrl;

    public UserDetailDto(Long uid, Long userName, Long followerCount, Long trackTotalCount, String pictureUrl) {
        this.uid = uid;
        this.userName = userName;
        this.followerCount = followerCount;
        this.trackTotalCount = trackTotalCount;
        this.pictureUrl = pictureUrl;
    }
}

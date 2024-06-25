package sky.Sss.domain.user.dto.myInfo;


import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.user.dto.UserInfoDto;
import sky.Sss.domain.user.model.UserGrade;
import sky.Sss.global.file.utili.FileStore;


@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserMyInfoDto implements Serializable {

    private String userId;
    private String email;
    private String userName;
    private String pictureUrl;
    private Boolean isLoginBlocked;
    private Boolean isAdmin;
    private List<Long> trackLikedIds;
    private List<Long> followingIds;
    private List<Long> followerIds;
    private Integer trackUploadCount;

    public UserMyInfoDto(String userId, String email, String userName, String pictureUrl, Boolean isLoginBlocked,
        UserGrade userGrade, List<Long> trackLikedIds, List<Long> followingIds,List<Long> followerIds,Integer trackUploadCount) {
        this.userId = userId;
        this.email = email;
        this.userName = userName;
        this.pictureUrl = pictureUrl;
        this.isLoginBlocked = isLoginBlocked;
        this.isAdmin = userGrade.equals(UserGrade.ADMIN);
        this.trackLikedIds = trackLikedIds;
        this.followingIds = followingIds;
        this.followerIds = followerIds;
        this.trackUploadCount = trackUploadCount;
    }



}

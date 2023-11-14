package sky.Sss.domain.user.dto.myInfo;


import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.user.dto.UserInfoDto;
import sky.Sss.domain.user.model.UserGrade;


@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserMyInfoDto implements Serializable {

    private String userId;
    private String email;
    private String userName;
    private String pictureUrl;
    private Boolean isLoginBlocked;
    private Boolean isMyProfile;
    private Boolean isAdmin;


    @Builder
    public UserProfileDto(String userId, String email, String userName, String pictureUrl, Boolean isLoginBlocked,
        Boolean isMyProfile, Boolean isAdmin) {
        this.userId = userId;
        this.email = email;
        this.userName = userName;
        this.pictureUrl = pictureUrl;
        this.isLoginBlocked = isLoginBlocked;
        this.isMyProfile = isMyProfile;
        this.isAdmin = isAdmin;
    }

    public static UserProfileDto createUseProfileDto(UserInfoDto userInfoDto) {
        return UserProfileDto.builder()
            .userId(userInfoDto.getUserId())
            .email(userInfoDto.getEmail())
            .pictureUrl(userInfoDto.getPictureUrl())
            .isLoginBlocked(userInfoDto.getIsLoginBlocked().getValue())
            .userName(userInfoDto.getUserName())
            .isMyProfile(userInfoDto.getIsMyProfile()).build();
    }


}

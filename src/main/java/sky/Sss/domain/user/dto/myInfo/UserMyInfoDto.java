package sky.Sss.domain.user.dto.myInfo;


import java.io.Serializable;
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


    @Builder
    public UserMyInfoDto(String userId, String email, String userName, String pictureUrl, Boolean isLoginBlocked,
        Boolean isAdmin) {
        this.userId = userId;
        this.email = email;
        this.userName = userName;
        this.pictureUrl = pictureUrl;
        this.isLoginBlocked = isLoginBlocked;
        this.isAdmin = isAdmin;
    }

    public UserMyInfoDto(String userId, String email, String userName, String pictureUrl, Boolean isLoginBlocked,
        UserGrade userGrade) {
        this.userId = userId;
        this.email = email;
        this.userName = userName;
        this.pictureUrl = pictureUrl == null ? FileStore.USER_DEFAULT_IMAGE_URL : pictureUrl;
        this.isLoginBlocked = isLoginBlocked;
        this.isAdmin = userGrade.equals(UserGrade.ADMIN);
    }

    public static UserMyInfoDto createUseProfileDto(UserInfoDto userInfoDto) {
        Optional<Boolean> isAdmin = userInfoDto.getGrantedAuthority().stream()
            .map(auth -> {
                auth.getAuthority();
                return false;
            }).findFirst();
        return UserMyInfoDto.builder()
            .userId(userInfoDto.getUserId())
            .email(userInfoDto.getEmail())
            .pictureUrl(userInfoDto.getPictureUrl())
            .isLoginBlocked(userInfoDto.getIsLoginBlocked().getValue())
            .userName(userInfoDto.getUserName())
            .isAdmin(isAdmin.orElse(false)).build();
    }


}

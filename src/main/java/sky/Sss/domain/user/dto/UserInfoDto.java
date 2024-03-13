package sky.Sss.domain.user.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sky.Sss.domain.user.dto.login.CustomUserDetails;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Blocked;
import sky.Sss.domain.user.model.Enabled;
import sky.Sss.global.file.utili.FileStore;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfoDto implements Serializable {


    private String userId;
    private String email;
    private String userName;
    private String token;
    private String pictureUrl;
    private Enabled enabled;
    private Blocked isLoginBlocked;
    private LocalDateTime userNameModifiedDate;
    private Boolean isMyProfile;
    private List<GrantedAuthority> grantedAuthority;

    @Builder
    private UserInfoDto(String userId, String email, String userName, String token,
        List<GrantedAuthority> grantedAuthority,
        String pictureUrl, LocalDateTime userNameModifiedDate, Enabled enabled,Blocked isLoginBlocked) {
        this.userId = userId;
        this.email = email;
        this.userName = userName;
        this.token = token;
        this.grantedAuthority = grantedAuthority;
        this.pictureUrl = pictureUrl;
        this.userNameModifiedDate = userNameModifiedDate;
        this.enabled = enabled;
        this.isLoginBlocked = isLoginBlocked;
    }

    public static UserInfoDto createUserInfo(UserDetails userDetails) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        return UserInfoDto.builder()
            .userId(customUserDetails.getUserId())
            .userName(customUserDetails.getNickname())
            .token(customUserDetails.getToken())
            .email(customUserDetails.getEmail())
            .userNameModifiedDate(customUserDetails.getUserNameModifiedDate())
            .enabled(Enabled.valueOf(userDetails.isEnabled()))
            .isLoginBlocked(Blocked.valueOf(customUserDetails.getLoginBlocked()))
            .pictureUrl(
                customUserDetails.getPictureUrl() == null ? FileStore.USER_DEFAULT_IMAGE_URL : customUserDetails.getPictureUrl())
            .grantedAuthority((List<GrantedAuthority>) customUserDetails.getAuthorities())
            .build();
    }

    public static UserInfoDto createUserInfo(User user) {
        UserDetails userDetails = User.UserBuilder(user);
        UserInfoDto userInfo = UserInfoDto.createUserInfo(userDetails);
        return userInfo;
    }

}

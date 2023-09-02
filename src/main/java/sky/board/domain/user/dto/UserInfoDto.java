package sky.board.domain.user.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import sky.board.domain.user.dto.login.CustomUserDetails;
import sky.board.domain.user.model.ImagePathDetails;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfoDto implements Serializable {


    private String userId;
    private String email;
    private String userName;
    private String token;
    private String pictureUrl;
    private LocalDateTime userNameModifiedDate;

    private List<GrantedAuthority> grantedAuthority;

    @Builder
    private UserInfoDto(String userId, String email, String userName, String token,
        List<GrantedAuthority> grantedAuthority,
        String pictureUrl, LocalDateTime userNameModifiedDate) {
        this.userId = userId;
        this.email = email;
        this.userName = userName;
        this.token = token;
        this.grantedAuthority = grantedAuthority;
        this.pictureUrl = pictureUrl;
        this.userNameModifiedDate = userNameModifiedDate;
    }

    public static UserInfoDto createUserInfo(CustomUserDetails userDetails) {
        return UserInfoDto.builder()
            .userId(userDetails.getUserId())
            .userName(userDetails.getNickname())
            .token(userDetails.getToken())
            .email(userDetails.getEmail())
            .userNameModifiedDate(userDetails.getUserNameModifiedDate())
            .pictureUrl(
                userDetails.getPictureUrl() == null ? ImagePathDetails.USER_DEFAULT_IMAGE : userDetails.getPictureUrl())
            .grantedAuthority((List<GrantedAuthority>) userDetails.getAuthorities())
            .build();
    }

}

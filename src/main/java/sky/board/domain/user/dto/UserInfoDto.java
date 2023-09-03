package sky.board.domain.user.dto;

import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sky.board.domain.user.dto.login.CustomUserDetails;
import sky.board.domain.user.entity.User;
import sky.board.global.file.utili.FileStore;
import sky.board.global.redis.dto.RedisKeyDto;

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

    public static UserInfoDto createUserInfo(UserDetails userDetails) {

        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        return UserInfoDto.builder()
            .userId(customUserDetails.getUserId())
            .userName(customUserDetails.getNickname())
            .token(customUserDetails.getToken())
            .email(customUserDetails.getEmail())
            .userNameModifiedDate(customUserDetails.getUserNameModifiedDate())
            .pictureUrl(
                customUserDetails.getPictureUrl() == null ? FileStore.USER_DEFAULT_IMAGE : customUserDetails.getPictureUrl())
            .grantedAuthority((List<GrantedAuthority>) customUserDetails.getAuthorities())
            .build();
    }

    public static void sessionUserInfoUpdate(HttpSession session, User user) {
        UserDetails userDetails = User.UserBuilder(user);
        UserInfoDto userInfo = UserInfoDto.createUserInfo((CustomUserDetails) userDetails);
        session.setAttribute(RedisKeyDto.USER_KEY, userInfo);
    }

}

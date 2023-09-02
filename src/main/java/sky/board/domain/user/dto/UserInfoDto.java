package sky.board.domain.user.dto;

import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sky.board.domain.user.dto.login.CustomUserDetails;
import sky.board.domain.user.entity.User;
import sky.board.global.file.FileStore;
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

    public static UserInfoDto createUserInfo(CustomUserDetails userDetails) {
        return UserInfoDto.builder()
            .userId(userDetails.getUserId())
            .userName(userDetails.getNickname())
            .token(userDetails.getToken())
            .email(userDetails.getEmail())
            .userNameModifiedDate(userDetails.getUserNameModifiedDate())
            .pictureUrl(
                userDetails.getPictureUrl() == null ? FileStore.USER_DEFAULT_IMAGE : userDetails.getPictureUrl())
            .grantedAuthority((List<GrantedAuthority>) userDetails.getAuthorities())
            .build();
    }

    public static void sessionUserInfoUpdate(HttpSession session, User user) {
        UserDetails userDetails = User.UserBuilder(user);
        UserInfoDto userInfo = UserInfoDto.createUserInfo((CustomUserDetails) userDetails);
        session.setAttribute(RedisKeyDto.USER_KEY, userInfo);
    }

}

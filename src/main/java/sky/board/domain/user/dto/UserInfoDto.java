package sky.board.domain.user.dto;

import java.io.Serializable;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import sky.board.domain.user.dto.login.CustomUserDetails;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfoDto implements Serializable {


    private String userId;
    private String username;
    private String token;
    private List<GrantedAuthority> grantedAuthority;

    @Builder
    private UserInfoDto(String userId, String username, String token, List<GrantedAuthority> grantedAuthority) {
        this.userId = userId;
        this.username = username;
        this.token = token;
        this.grantedAuthority = grantedAuthority;
    }

    public static UserInfoDto createUserInfo(CustomUserDetails userDetails) {
        return UserInfoDto.builder()
            .userId(userDetails.getUserId())
            .username(userDetails.getNickname())
            .token(userDetails.getToken())
            .grantedAuthority((List<GrantedAuthority>) userDetails.getAuthorities()).build();
    }

}

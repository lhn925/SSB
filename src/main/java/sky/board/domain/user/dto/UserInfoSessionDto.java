package sky.board.domain.user.dto;

import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import sky.board.domain.user.dto.login.CustomUserDetails;

@Getter
public class UserInfoSessionDto implements Serializable {


    private String userId;
    private String username;
    private String token;
    private List<GrantedAuthority> grantedAuthority;

    protected UserInfoSessionDto(CustomUserDetails userDetails) {
        this.userId = userDetails.getUserId();
        this.username = userDetails.getUsername();
        this.token = userDetails.getToken();
        this.grantedAuthority = (List<GrantedAuthority>) userDetails.getAuthorities();
    }

    public static UserInfoSessionDto createUserInfo (CustomUserDetails userDetails) {
        UserInfoSessionDto userInfoSessionDto = new UserInfoSessionDto(userDetails);
        return userInfoSessionDto;
    }

}

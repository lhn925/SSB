package sky.board.domain.user.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;

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

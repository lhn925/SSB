package sky.board.domain.user.dto.myInfo;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.board.domain.user.entity.login.UserLoginStatus;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLoginListDto {

    private String os;
    private String browser;
    private String ip;
    private String countryName;
    // 로그인 날짜
    private LocalDateTime createdDateTime;

    // 현재 접속한 세션과 일치 여부
    private Boolean inSession;


    private String session;
    // 로그인 상태
    private Boolean loginStatus;

    public UserLoginListDto(String inSession, UserLoginStatus userLoginStatus) {
        // session 값이 같으면 null 아니면 sessionId값 그대로
        this.session = userLoginStatus.getSession().equals(inSession) ? null : userLoginStatus.getSession();
        this.os = userLoginStatus.getOs();
        this.browser = userLoginStatus.getBrowser();
        this.ip = userLoginStatus.getDefaultLoginLog().getIp();
        this.countryName = userLoginStatus.getDefaultLoginLog().getCountryName();
        this.createdDateTime = userLoginStatus.getCreatedDateTime();
        this.loginStatus = userLoginStatus.getLoginStatus();
        this.inSession = inSession.equals(userLoginStatus.getSession());
    }
}

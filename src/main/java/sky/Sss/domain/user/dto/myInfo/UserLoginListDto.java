package sky.Sss.domain.user.dto.myInfo;


import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.user.entity.login.UserLoginStatus;

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
        this.session = userLoginStatus.getSessionId().equals(inSession) ? null : userLoginStatus.getSessionId();
        this.os = userLoginStatus.getOs();
        this.browser = userLoginStatus.getBrowser();
        this.ip = userLoginStatus.getDefaultLocationLog().getIp();
        this.countryName = userLoginStatus.getDefaultLocationLog().getCountryName();
        this.createdDateTime = userLoginStatus.getCreatedDateTime();
        this.loginStatus = userLoginStatus.getLoginStatus();
        this.inSession = inSession.equals(userLoginStatus.getSessionId());
    }
}

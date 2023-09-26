package sky.Sss.domain.user.dto.myInfo;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.user.entity.login.UserLoginLog;
import sky.Sss.domain.user.model.UserAgent;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLoginLogListDto implements Serializable {

    private LocalDateTime createdDateTime;
    private String ip;
    private String countryCode;
    private UserAgent userAgent;

    public UserLoginLogListDto(UserLoginLog userLoginLog) {
        this.createdDateTime = userLoginLog.getCreatedDateTime();
        this.ip = userLoginLog.getDefaultLoginLog().getIp();
        this.countryCode = userLoginLog.getDefaultLoginLog().getCountryName();
        this.userAgent = userLoginLog.getDefaultLoginLog().getUserAgent();
    }
}

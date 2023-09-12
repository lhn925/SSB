package sky.board.domain.user.dto.myInfo;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.board.domain.user.entity.login.UserLoginLog;
import sky.board.domain.user.model.UserAgent;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLoginLogListDto implements Serializable {

    private LocalDateTime createDateTime;
    private String ip;
    private String countryCode;
    private UserAgent userAgent;

    public UserLoginLogListDto(UserLoginLog userLoginLog) {
        this.createDateTime = userLoginLog.getCreatedDateTime();
        this.ip = userLoginLog.getDefaultLoginLog().getIp();
        this.countryCode = userLoginLog.getDefaultLoginLog().getCountryName();
        this.userAgent = userLoginLog.getDefaultLoginLog().getUserAgent();
    }
}

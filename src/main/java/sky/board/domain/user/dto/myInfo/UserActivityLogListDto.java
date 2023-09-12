package sky.board.domain.user.dto.myInfo;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.board.domain.user.entity.UserActivityLog;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserActivityLogListDto implements Serializable {

    private LocalDateTime createDateTime;

    //변경내용
    private String chaContent;

    // 변경 방법
    private String chaMethod;
    private String ip;

    public UserActivityLogListDto(UserActivityLog userActivityLog) {
        this.createDateTime = userActivityLog.getCreatedDateTime();
        this.chaContent = userActivityLog.getChaContent();
        this.chaMethod = userActivityLog.getChaMethod();
        this.ip = userActivityLog.getIp();
    }
}

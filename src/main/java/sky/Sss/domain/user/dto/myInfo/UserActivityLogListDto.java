package sky.Sss.domain.user.dto.myInfo;

import jakarta.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.MessageSource;
import sky.Sss.domain.user.entity.UserActivityLog;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserActivityLogListDto implements Serializable {

    private LocalDateTime createdDateTime;

    //변경내용
    private String chaContent;

    // 변경 방법
    private String chaMethod;
    private String ip;

    public UserActivityLogListDto(MessageSource ms, HttpServletRequest request, UserActivityLog userActivityLog) {
        this.createdDateTime = userActivityLog.getCreatedDateTime();
        this.chaContent = ms.getMessage(userActivityLog.getChaContent(), null, request.getLocale());
        this.chaMethod = ms.getMessage(userActivityLog.getChaMethod(), null, request.getLocale());
        this.ip = userActivityLog.getIp();
    }
}

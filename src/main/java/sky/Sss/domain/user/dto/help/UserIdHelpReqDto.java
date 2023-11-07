package sky.Sss.domain.user.dto.help;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserIdHelpRepDto implements Serializable {
    private String userId;
    /**
     * 암호화 되어 있는 이메일
     */
    @DateTimeFormat(pattern = "yyyy:MM:dd HH:mm:ss")
    private LocalDateTime createdDateTime;

    @Builder
    public UserIdHelpRepDto(String userId, LocalDateTime createdDateTime) {
        this.userId = userId;
        this.createdDateTime = createdDateTime;
    }


}

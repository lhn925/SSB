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
public class UserIdHelpReqDto implements Serializable {
    private String email;
    private String authToken;

    @Builder
    public UserIdHelpReqDto(String email, String authToken) {
        this.email = email;
        this.authToken = authToken;
    }
}

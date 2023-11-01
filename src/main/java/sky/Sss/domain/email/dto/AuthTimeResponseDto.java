package sky.Sss.domain.email.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthTimeResponseDto implements Serializable  {
    private LocalDateTime authTimeLimit; // 인증 코드 유효 시간
    private LocalDateTime authIssueTime; //인증 코드 발급 시간
    private String authToken; //인증 코드 발급 시간

    public AuthTimeResponseDto(LocalDateTime authTimeLimit, LocalDateTime authIssueTime,String authToken) {
        this.authTimeLimit = authTimeLimit;
        this.authIssueTime = authIssueTime;
        this.authToken = authToken;
    }


}
package sky.Sss.domain.email.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EmailAuthCodeDto implements Serializable  {
    private String code;
    private String email;
    private LocalDateTime authTimeLimit;
    private String authToken;
    private Boolean isSuccess; // 이메일 인증 성공 여부

    @Builder
    public EmailAuthCodeDto(String code, LocalDateTime authTimeLimit,Boolean isSuccess,String email,String authToken) {
        this.code = code;
        this.authTimeLimit = authTimeLimit;
        this.isSuccess = isSuccess;
        this.email = email;
        this.authToken = authToken;
    }

    public void changeSuccess (Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

}
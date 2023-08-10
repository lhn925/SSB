package sky.board.domain.email.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
public class EmailAuthCodeDto implements Serializable  {
    private String code;
    private LocalDateTime authTimeLimit;
    private Boolean isSuccess; // 이메일 인증 성공 여부

    public EmailAuthCodeDto(String code, LocalDateTime authTimeLimit,Boolean isSuccess) {
        this.code = code;
        this.authTimeLimit = authTimeLimit;
        this.isSuccess = isSuccess;
    }

    public void changeSuccess (Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

}
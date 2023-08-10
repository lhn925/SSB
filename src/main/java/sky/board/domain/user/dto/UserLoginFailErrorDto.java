package sky.board.domain.user.dto;


import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginFailErrorDto implements Serializable  {

    // 에러 메시지
    private String errMsg;

    // 에러 여부
    private boolean error;

    // 5번 틀렸을 경우 true:2차 인증
    private boolean retryTwoFactor;
}

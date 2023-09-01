package sky.board.domain.user.dto.login;


import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLoginFailErrorDto implements Serializable  {

    // 에러 메시지
    private String errMsg;

    // 에러 여부
    private boolean error;

    // 5번 틀렸을 경우 true:2차 인증
    private boolean retryTwoFactor;
}

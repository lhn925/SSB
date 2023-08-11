package sky.board.domain.user.dto;


import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginFormDto implements Serializable {
    private String url;
    private String mode;
    private String userId;

    private Boolean rememberMe;

    // 인증번호
    private String captcha;

    // 인증키
    private String captchaKey;

    // 이미지경로
    private String imagePath;
}
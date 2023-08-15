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
    private String password;
    private Boolean rememberMe;

    // 인증번호
    private String captcha;

    // 인증키
    private String captchaKey;

    // 이미지 이름
    private String imageName;
}
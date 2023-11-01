package sky.Sss.domain.user.dto.login;


import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLoginFormDto implements Serializable {

    @NotBlank
    private String userId;

    @NotBlank
    private String password;



    // 인증번호
    private String captcha;

    // 인증키
    private String captchaKey;

    // 이미지 이름
    private String imageName;

    private String message;
}
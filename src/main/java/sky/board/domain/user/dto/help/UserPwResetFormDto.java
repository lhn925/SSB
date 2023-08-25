package sky.board.domain.user.dto.help;


import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.board.domain.user.annotation.JoinValid;
import sky.board.domain.user.model.PwSecLevel;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPwResetFormDto implements Serializable {

    @NotBlank
    private String userId;
    @NotBlank
    private String helpToken;

    private String newPw;

    private String newPwChk;

    private PwSecLevel pwSecLevel;
    // 인증번호
//    @NotBlank
    private String captcha;
    // 인증키
//    @NotBlank
    private String captchaKey;

    // 이미지 이름
//    @NotBlank
    private String imageName;

    @Builder
    public UserPwResetFormDto(String userId, String helpToken, String newPw, String newPwChk, String captcha,
        String captchaKey, String imageName) {
        this.userId = userId;
        this.helpToken = helpToken;
        this.newPw = newPw;
        this.newPwChk = newPwChk;
        this.captcha = captcha;
        this.captchaKey = captchaKey;
        this.imageName = imageName;
    }
}

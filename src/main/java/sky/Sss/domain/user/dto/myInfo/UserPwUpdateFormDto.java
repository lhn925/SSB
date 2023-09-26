package sky.Sss.domain.user.dto.myInfo;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.user.model.PwSecLevel;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPwUpdateFormDto {

    private String password;
    private String newPw;

    private String newPwChk;

    private PwSecLevel pwSecLevel;
    // 인증번호
    @NotBlank
    private String captcha;
    // 인증키
    @NotBlank
    private String captchaKey;

    // 이미지 이름
    @NotBlank
    private String imageName;

    @Builder
    public UserPwUpdateFormDto(String password, String newPw, String newPwChk, PwSecLevel pwSecLevel,
        String captcha, String captchaKey, String imageName) {
        this.password = password;
        this.newPw = newPw;
        this.newPwChk = newPwChk;
        this.pwSecLevel = pwSecLevel;
        this.captcha = captcha;
        this.captchaKey = captchaKey;
        this.imageName = imageName;
    }
}

package sky.board.domain.user.dto.myInfo;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.board.domain.user.model.PwSecLevel;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPwUpdateFormDto {


    @NotBlank
    private String userId;

    private String password;
    private String updatePw;

    private String updatePwChk;

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
    public UserPwUpdateFormDto(String userId, String password, String updatePw, String updatePwChk, PwSecLevel pwSecLevel,
        String captcha, String captchaKey, String imageName) {
        this.userId = userId;
        this.password = password;
        this.updatePw = updatePw;
        this.updatePwChk = updatePwChk;
        this.pwSecLevel = pwSecLevel;
        this.captcha = captcha;
        this.captchaKey = captchaKey;
        this.imageName = imageName;
    }
}

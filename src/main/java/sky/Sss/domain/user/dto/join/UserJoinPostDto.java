package sky.Sss.domain.user.dto.join;


import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.domain.user.annotation.JoinValid;
import sky.Sss.domain.user.model.PwSecLevel;
import sky.Sss.global.utili.validation.regex.RegexPatterns;

@Getter
@Setter
public class UserJoinPostDto implements Serializable {

    /**
     * 아이디: 5~20자의 영문 소문자, 숫자와 특수기호(_),(-)만 사용 가능합니다.
     * 비밀번호: 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.
     * 악성/타유저 유사 닉네임 사용할 경우 영구 차단 될 수 있습니다.
     * 정치 관련, 특정 사이트 언급, 반사회적, 성적, 욕설 닉네임은 금지합니다.
     * 1달에 1번 닉네임 변경 가능합니다.
     */

    @JoinValid(regexp = RegexPatterns.USER_ID_REGEX, message = "{userJoinForm.userId}")
    private String userId;

//    @JoinValid(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "{userJoinForm.password}")
    @NotBlank
    private String password;

    @JoinValid(regexp = RegexPatterns.USER_NAME_REGEX, message = "{userJoinForm.userName}")
    private String userName;

    @JoinValid(regexp = RegexPatterns.EMAIL_REGEX, message = "{userJoinForm.email}")
    private String email;

    private String authCode;

    @AssertTrue(message = "{userJoinAgree.AssertTrue}")
    private boolean sbbAgreement;

    @AssertTrue(message = "{userJoinAgree.AssertTrue}")
    private boolean infoAgreement;

    // 보안 레벨
    private PwSecLevel pwSecLevel;

    public String changePassword(String password) {
        return this.password = password;
    }
}

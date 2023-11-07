package sky.Sss.domain.user.dto.help;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import sky.Sss.domain.email.model.SendType;
import sky.Sss.domain.user.annotation.JoinValid;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserHelpDto implements Serializable {

    private String userId;

    @JoinValid(regexp = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$", message = "{userJoinForm.email}")
    private String email;

    /**
     * 암호화 되어 있는 이메일
     */
    private String enEmail;

    private String authCode;

    @NotBlank(message = "{NotBlank.authToken}")
    private String authToken;

    private String helpType;

    public static UserHelpDto createUserHelpDto() {
        UserHelpDto userHelpDto = new UserHelpDto();
        return userHelpDto;
    }

}

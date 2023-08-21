package sky.board.domain.user.dto.help;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.board.domain.user.annotation.JoinValid;
import sky.board.domain.user.utill.UserTokenUtil;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserHelpIdDto {

    private String userId;


    @JoinValid(regexp = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$", message = "{userJoinForm.email}")
    private String email;

    @NotBlank
    private String helpToken;

    private String authCode;

    private LocalDateTime createdDateTime;

    public static UserHelpIdDto createUserHelpIdDto() {
        UserHelpIdDto userJoinAgreeResponseDto = new UserHelpIdDto();
        String token = UserTokenUtil.getToken();
        userJoinAgreeResponseDto.setHelpToken(token);
        return userJoinAgreeResponseDto;
    }

}

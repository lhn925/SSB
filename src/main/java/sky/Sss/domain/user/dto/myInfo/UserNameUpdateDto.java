package sky.Sss.domain.user.dto.myInfo;


import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.domain.user.annotation.JoinValid;
import sky.Sss.global.utili.validation.regex.RegexPatterns;

@Getter
@Setter
public class UserNameUpdateDto implements Serializable {

    @JoinValid(regexp = RegexPatterns.USER_NAME_REGEX, message = "{userJoinForm.userName}")
    private String userName;

    private LocalDateTime userNameModifiedDate;

}

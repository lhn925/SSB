package sky.Sss.domain.user.dto.join;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.user.annotation.JoinValid;
import sky.Sss.global.utili.validation.regex.RegexPatterns;


/**
 * 아이디 중복 Dto
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinEmailDuplicateDto implements Serializable  {

    @JoinValid(regexp = RegexPatterns.EMAIL_REGEX, message = "{userJoinForm.email}")
    private String email;
}

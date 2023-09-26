package sky.Sss.domain.user.dto.join;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.user.annotation.JoinValid;


/**
 * 아이디 중복 Dto
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinUserNameDuplicateDto implements Serializable  {

    @JoinValid(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,8}$", message = "{userJoinForm.userName}")
    private String userName;
}

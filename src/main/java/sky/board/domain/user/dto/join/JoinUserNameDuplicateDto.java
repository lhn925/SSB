package sky.board.domain.user.dto.join;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import sky.board.domain.user.annotation.JoinValid;


/**
 * 아이디 중복 Dto
 */
@Getter
@Setter
public class JoinUserNameDuplicateDto implements Serializable  {

    @JoinValid(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,8}$", message = "{userJoinForm.userName}")
    private String userName;
}

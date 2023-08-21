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
public class JoinEmailDuplicateDto implements Serializable  {

    @JoinValid(regexp = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$", message = "{userJoinForm.email}")
    private String email;
}

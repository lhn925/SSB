package sky.board.domain.user.dto;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import sky.board.domain.user.annotation.JoinValid;


/**
 * 아이디 중복 Dto
 */
@Getter
@Setter
public class JoinIdDuplicateDto implements Serializable  {

    @JoinValid(regexp = "^[a-z0-9_-]{5,20} *$", message = "{userJoinForm.userId}")
    private String userId;
}

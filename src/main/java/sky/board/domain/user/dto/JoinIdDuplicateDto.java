package sky.board.domain.user.dto;

import lombok.Getter;
import lombok.Setter;
import sky.board.domain.user.annotation.JoinValid;


/**
 * 아이디 중복 Dto
 */
@Getter
@Setter
public class JoinIdDuplicateDto {

    @JoinValid(regexp = "^[a-z0-9_-]{5,20} *$", message = "{userJoinForm.userId}")
    private String userId;
}

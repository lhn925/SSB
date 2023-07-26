package sky.board.domain.user.dto;

import lombok.Getter;
import lombok.Setter;


/**
 * 중복 측정
 */
@Getter
@Setter
public class JoinDuplicateDto {
    private String userId;
    private String userName;
}

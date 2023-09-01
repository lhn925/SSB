package sky.board.domain.user.dto.help;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class UserIdHelpQueryDto implements Serializable {
    private String userId;
}

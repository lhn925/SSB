package sky.Sss.domain.user.dto.help;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserIdQueryDto implements Serializable {
    @NotBlank
    private String userId;
}

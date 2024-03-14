package sky.Sss.domain.user.dto.myInfo;


import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginStatusUpdateDto implements Serializable {

    @NotBlank
    private String password;
    @NotBlank
    private String session;
}

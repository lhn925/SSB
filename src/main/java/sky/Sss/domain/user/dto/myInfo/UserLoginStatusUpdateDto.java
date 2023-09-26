package sky.Sss.domain.user.dto.myInfo;


import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginStatusUpdateDto implements Serializable {

    private String session;
}

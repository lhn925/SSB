package sky.board.domain.user.dto.myInfo;


import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import sky.board.domain.user.annotation.JoinValid;

@Getter
@Setter
public class UserNameUpdateDto implements Serializable {

    @JoinValid(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,8}$", message = "{userJoinForm.userName}")
    private String userName;

    private LocalDateTime userNameModifiedDate;

}

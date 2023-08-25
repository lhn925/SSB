package sky.board.domain.email.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import sky.board.domain.user.model.HelpType;

@Getter
@Setter
public class HelpEmailSendDto extends EmailSendDto implements Serializable{

    private String userId;
    private HelpType helpType;
}
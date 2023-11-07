package sky.Sss.domain.email.dto;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.domain.email.model.SendType;
import sky.Sss.domain.user.model.HelpType;

@Getter
@Setter
public class HelpEmailSendDto extends EmailSendDto implements Serializable{

    private String userId;
    private HelpType helpType;
}

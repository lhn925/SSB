package sky.Sss.domain.user.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BrowserSession {
    private String sessionId;
    private String userId;
    private String uuid;
}

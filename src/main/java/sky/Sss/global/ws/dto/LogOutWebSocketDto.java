package sky.Sss.global.ws.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogOutWebSocketDto {
    private String userId;
    private String sessionId;
    private String name;
    private String data;
}

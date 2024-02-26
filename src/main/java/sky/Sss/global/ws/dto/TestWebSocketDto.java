package sky.Sss.global.ws.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestWebSocketDto {
    private String userId;
    private String message;
    private String sessionId;
}

package sky.board.global.openapi.dto;

import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class CaptchaNkeyDto implements Serializable {
    private String captchaKey;
    private String imageName;

    @Builder
    public CaptchaNkeyDto(String captchaKey, String imageName) {
        this.captchaKey = captchaKey;
        this.imageName = imageName;
    }
}

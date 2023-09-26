package sky.Sss.global.openapi.dto;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaptchaNkeyDto implements Serializable {
    @NotBlank
    private String captchaKey;
    @NotBlank
    private String imageName;

    @Builder
    public CaptchaNkeyDto(String captchaKey, String imageName) {
        this.captchaKey = captchaKey;
        this.imageName = imageName;
    }
}

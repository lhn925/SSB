package sky.Sss.global.openapi.entity;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class CaptchaOpenApi {
    private String clientId;
    private String clientSecret;

    @Value("${openapi.relaxed-binding.clientId}")
    private void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Value("${openapi.relaxed-binding.clientSecret}")
    private void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}

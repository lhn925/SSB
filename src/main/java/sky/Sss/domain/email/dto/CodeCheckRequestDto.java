package sky.Sss.domain.email.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.domain.email.model.SendType;

@Getter
@Setter
public class CodeCheckRequestDto implements Serializable  {
    @NotBlank(message = "{NotBlank.authCode}")
    private String authCode;

    @NotBlank(message = "{NotBlank.authToken}")
    private String authToken;

    @NotNull(message = "{NotBlank.authToken}")
    private SendType sendType;

}

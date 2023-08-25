package sky.board.domain.email.dto;


import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeCheckRequestDto implements Serializable  {
    @NotBlank(message = "NotBlank.authCode")
    private String authCode;

}

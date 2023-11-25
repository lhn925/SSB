package sky.Sss.domain.email.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.domain.email.model.SendType;
import sky.Sss.global.utili.validation.regex.RegexPatterns;

@Getter
@Setter
public class EmailSendDto implements Serializable  {
    @NotBlank
    @Pattern(regexp = RegexPatterns.EMAIL_REGEX,message = "{userJoinForm.email}")
    private String email; // 유저

    @NotNull(message = "{NotBlank}")
    private SendType sendType;
}

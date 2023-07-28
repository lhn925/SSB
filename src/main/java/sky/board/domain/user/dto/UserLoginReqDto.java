package sky.board.domain.user.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserLoginReqDto {


    @NotBlank
    private String userId;
    @NotBlank
    private String password;
}

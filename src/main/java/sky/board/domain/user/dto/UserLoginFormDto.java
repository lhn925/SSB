package sky.board.domain.user.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginFormDto {
    private String url;
    private String mode;
    private String userId;
    private String captcha;
}
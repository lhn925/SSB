package sky.Sss.domain.board.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BoardForm {

    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String text;

    private Long views;

    private Long referrals;

}

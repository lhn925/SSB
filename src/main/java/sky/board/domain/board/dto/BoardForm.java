package sky.board.domain.board.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;

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

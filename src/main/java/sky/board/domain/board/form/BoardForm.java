package sky.board.domain.board.form;


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

    private String nickname;

    @DateTimeFormat(pattern = "yyyy:MM:dd HH:mm:ss")
    private Date date;

    private Long views;

    private Long referrals;

}

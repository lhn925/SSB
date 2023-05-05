package sky.board.domain.board.entity;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.sql.Date;

@Data
public class Board {

    @NotBlank
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String text;

    @NotBlank
    private String nickname;

    private Date date;

    private Long views;

    private Long referrals;



}

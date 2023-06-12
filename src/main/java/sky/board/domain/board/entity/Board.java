package sky.board.domain.board.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;
import sky.board.domain.board.dto.BoardForm;
import sky.board.domain.user.entity.User;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter(value = AccessLevel.PRIVATE)
@Entity
public class Board {


    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String content;

    private Long views; // 조회수

    private int total_rec; // 추천 수

    private String createByUsername; // 글쓴 사람 닉네임

    @CreatedBy
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User createByUser; // 글쓴 유저의 pk값

    @CreatedDate
    @DateTimeFormat(pattern = "yyyy:MM:dd HH:mm:ss")
    private LocalDateTime createDateTime;

    @LastModifiedDate
    @DateTimeFormat(pattern = "yyyy:MM:dd HH:mm:ss")
    private LocalDateTime modifiedDateTime;

    public static Board createBoard (BoardForm boardForm) {
        Board board = new Board();
        board.setTitle(boardForm.getTitle());
        board.setContent(boardForm.getText());
        return board;
    }

}

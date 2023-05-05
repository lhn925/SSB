package sky.board.domain.board.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.board.domain.board.entity.Board;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class BoardJdbcRepositoryTest {


    @Autowired
    BoardRepository boardRepository;

    @Test
    void save () {
        Board board = new Board();
        board.setNickname("iu");
        board.setTitle("안녕하세요");
        board.setText("내용입니다 <img src='/'>");
//        board.setDate(LocalDateTime.now());
        Integer save = boardRepository.save(board);
    }

    @Test
    void findById () {
        Optional<Board> optionalBoard = boardRepository.findById(100L);

        Board findBoard = optionalBoard.orElseGet(() -> null);
        assertThat(findBoard).isNotNull();
        Map<String, Object> map = new HashMap<>();
        optionalBoard.ifPresent(board -> map.put("board", board));

        assertThat(map.get("board")).isNotNull();
    }

    @Test
    void findByAll () {
        List<Board> boardList = boardRepository.findByList(0, 10);

        for (Board board : boardList) {
            System.out.println("=========================================");
            System.out.println("board.getId() = " + board.getId());
            System.out.println("board.getTitle() = " + board.getTitle());
            System.out.println("board.getText() = " + board.getText());
        }
    }
}
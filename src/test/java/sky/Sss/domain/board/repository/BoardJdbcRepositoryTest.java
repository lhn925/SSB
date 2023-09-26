package sky.Sss.domain.board.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.Sss.domain.board.entity.Board;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class BoardJdbcRepositoryTest {


    @Autowired
    EntityManager em;
    @Autowired
    BoardRepository boardRepository;

    @Test
    void save () {


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
            System.out.println("board.getText() = " + board.getContent());
        }
    }
}
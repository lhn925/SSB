package sky.board.domain.board.service;

import java.util.List;
import java.util.Optional;
import sky.board.domain.board.dto.BoardForm;
import sky.board.domain.board.entity.Board;

public interface BoardService {

    Long save(BoardForm boardForm);

    Board findById(Long findId);

    List<Board> findByList(int page, int end);
}

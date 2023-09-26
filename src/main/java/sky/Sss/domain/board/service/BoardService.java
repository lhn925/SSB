package sky.Sss.domain.board.service;

import java.util.List;
import sky.Sss.domain.board.dto.BoardForm;
import sky.Sss.domain.board.entity.Board;

public interface BoardService {

    Long save(BoardForm boardForm);

    Board findById(Long findId);

    List<Board> findByList(int page, int end);
}

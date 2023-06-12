package sky.board.domain.board.service;


import sky.board.domain.board.entity.Board;
import sky.board.domain.board.dto.BoardForm;

import java.util.List;

public interface boardService {

    Long save (BoardForm boardForm);

    Board findById (Long findId);

    List<Board> findByList (int start, int end);


}

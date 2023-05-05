package sky.board.domain.board.service;


import org.springframework.http.ResponseEntity;
import sky.board.domain.board.entity.Board;
import sky.board.domain.board.form.BoardForm;

import java.util.List;

public interface boardService {

    Integer save (BoardForm boardForm);

    Board findById (Long findId);

    List<Board> findByList (int page, int end);


}

package sky.board.domain.board.repository;

import sky.board.domain.board.entity.Board;

import java.util.List;
import java.util.Optional;

public interface BoardRepository {

    Integer save (Board board);
    Optional<Board> findById(Long findId);
    List<Board> findByList(int page,int end);
}

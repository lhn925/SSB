package sky.Sss.domain.board.repository;

import sky.Sss.domain.board.entity.Board;

import java.util.List;
import java.util.Optional;

public interface BoardRepository {

    void save (Board board);
    Optional<Board> findById(Long findId);
    List<Board> findByList(int start,int end);
}

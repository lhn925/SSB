package sky.Sss.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.board.entity.Board;
import sky.Sss.domain.board.dto.BoardForm;
import sky.Sss.domain.board.repository.BoardRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardImplService implements BoardService {

    private final BoardRepository boardRepository;

    @Transactional
    @Override
    public Long save (BoardForm boardForm) {
        Board board = Board.createBoard(boardForm);
        boardRepository.save(board);
        return board.getId();
    }

    @Override
    public Board findById (Long findId) {
        Optional<Board> optId = boardRepository.findById(findId);
        return optId.orElseGet(() -> null);
    }

    @Override
    public List<Board> findByList (int page, int end) {
        int start = 10 * (page - 1);
        return boardRepository.findByList(start, end);
    }
}

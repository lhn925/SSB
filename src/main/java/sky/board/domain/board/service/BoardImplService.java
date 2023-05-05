package sky.board.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sky.board.domain.board.entity.Board;
import sky.board.domain.board.form.BoardForm;
import sky.board.domain.board.repository.BoardRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardImplService implements boardService {

    private final BoardRepository boardRepository;

    @Override
    public Integer save (BoardForm boardForm) {
        Board board = new Board();
        board.setTitle(boardForm.getTitle());
        board.setText(boardForm.getText());
        board.setNickname(boardForm.getNickname());
        Integer save = boardRepository.save(board);
        return save;
    }

    @Override
    public Board findById (Long findId) {
        Optional<Board> optId = boardRepository.findById(findId);
        return optId.orElseGet(() -> null);
    }

    @Override
    public List<Board> findByList (int page, int end) {
        return boardRepository.findByList(page, end);
    }
}

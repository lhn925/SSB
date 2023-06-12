package sky.board.domain.board.repository;


import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sky.board.domain.board.entity.Board;

import java.util.List;
import java.util.Optional;


@Slf4j
@Repository
@RequiredArgsConstructor
public class BoardJpaRepository implements BoardRepository {

    private final EntityManager em;

    @Override
    public void save (Board board) {
        em.persist(board);
    }

    @Override
    public Optional<Board> findById (Long findId) {
        Board board = em.find(Board.class, findId);
        return Optional.ofNullable(board);
    }

    @Override
    public List<Board> findByList (int start, int end) {
       return em.createQuery("select b from Board b",Board.class).setFirstResult(start).setMaxResults(end).getResultList();
    }
}

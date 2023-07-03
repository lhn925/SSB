package sky.board.domain.board.repository;


import static sky.board.domain.board.entity.QBoard.board;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import sky.board.domain.board.entity.Board;

import java.util.List;
import java.util.Optional;


@Slf4j
@Repository
public class BoardJpaRepository implements BoardRepository {

    private final EntityManager em;
    private final JPAQueryFactory query;

    public BoardJpaRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public void save(Board board) {
        em.persist(board);
    }

    @Override
    public Optional<Board> findById(Long findId) {
        Board board = em.find(Board.class, findId);
        return Optional.ofNullable(board);
    }

    @Override
    public List<Board> findByList(int start, int end) {
        return query.selectFrom(board)
            .offset(start)
            .limit(end)
            .fetch();
    }
}

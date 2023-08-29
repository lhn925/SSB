package sky.board.domain.user.repository.join;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sky.board.domain.user.entity.User;


@Repository
public interface UserJoinRepository extends JpaRepository<User,Long> {
}

package sky.board.domain.user.repository.join;

import org.springframework.data.jpa.repository.JpaRepository;
import sky.board.domain.user.entity.UserJoinAgreement;

public interface UserAgreeRepository extends JpaRepository<UserJoinAgreement, Long> {

}

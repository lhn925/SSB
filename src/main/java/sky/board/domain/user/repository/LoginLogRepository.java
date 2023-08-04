package sky.board.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sky.board.domain.user.entity.UserLoginLog;

public interface LoginLogRepository extends JpaRepository<UserLoginLog, Long> {


}

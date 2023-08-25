package sky.board.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sky.board.domain.user.entity.UserActivityLog;

public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {



}

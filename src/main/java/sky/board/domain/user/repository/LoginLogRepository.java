package sky.board.domain.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sky.board.domain.user.entity.UserLoginLog;
import sky.board.domain.user.model.LoginSuccess;

public interface LoginLogRepository extends JpaRepository<UserLoginLog, Long> {

    Page<UserLoginLog> findByUserIdAndIsSuccess(String userId, LoginSuccess isSuccess, Pageable pageable);
}

package sky.board.domain.user.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.board.domain.user.entity.UserLoginLog;
import sky.board.domain.user.model.LoginSuccess;
import sky.board.domain.user.model.Status;

public interface LoginLogRepository extends JpaRepository<UserLoginLog, Long> {

    //
    Page<UserLoginLog> findByUserIdAndIsSuccessAndIsStatus(String userId, LoginSuccess isSuccess, Boolean status,
        Pageable pageable);


    //
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE UserLoginLog u SET u.isStatus = :isStatus where u.userId = :userId AND u.isSuccess =:isSuccess")
    void isStatusUpdate(@Param("userId") String userId, @Param("isSuccess") LoginSuccess isSuccess,
        @Param("isStatus") boolean isStatus);
}

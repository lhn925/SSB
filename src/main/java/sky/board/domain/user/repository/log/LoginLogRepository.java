package sky.board.domain.user.repository.log;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.board.domain.user.entity.login.UserLoginLog;
import sky.board.domain.user.model.LoginSuccess;

public interface LoginLogRepository extends JpaRepository<UserLoginLog, Long> {

    //
    @Query(value = "select u from UserLoginLog u where "
        + "u.defaultLoginLog.userId = :userId and "
        + "u.isSuccess = :isSuccess and "
        + "u.defaultLoginLog.isStatus = :isStatus ")
    Page<UserLoginLog> getLoginLogPageable(@Param("userId") String userId,
        @Param("isSuccess") LoginSuccess isSuccess, @Param("isStatus") Boolean status,
        Pageable pageable);

    //
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE UserLoginLog u SET u.defaultLoginLog.isStatus = :isStatus where u.defaultLoginLog.userId = :userId AND u.isSuccess =:isSuccess")
    void isStatusUpdate(@Param("userId") String userId, @Param("isSuccess") LoginSuccess isSuccess,
        @Param("isStatus") boolean isStatus);

    //
    @Query(value = "select u from UserLoginLog u where "
        + "u.defaultLoginLog.userId = :userId and "
        + "u.isSuccess = :isSuccess and "
        + "u.defaultLoginLog.isStatus = :isStatus and DATE_FORMAT(u.createdDateTime,'%Y-%m-%d') between :startDate and :endDate")
    Page<UserLoginLog> getLoginLogPageable(@Param("userId") String userId,
        @Param("isSuccess") LoginSuccess isSuccess,
        @Param("isStatus") Boolean status,
        @Param("startDate") LocalDate startDate,
        @Param("endDate")  LocalDate endDate,
        Pageable pageable);
}

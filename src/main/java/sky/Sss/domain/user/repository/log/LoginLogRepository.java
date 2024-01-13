package sky.Sss.domain.user.repository.log;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.user.entity.login.UserLoginLog;
import sky.Sss.domain.user.model.LoginSuccess;

public interface LoginLogRepository extends JpaRepository<UserLoginLog, Long> {

    //
    @Query(value = "select u from UserLoginLog u where "
        + "u.defaultLocationLog.userId = :userId and "
        + "u.isSuccess = :isSuccess and "
        + "u.defaultLocationLog.isStatus = :isStatus ")
    Page<UserLoginLog> getLoginLogPageable(@Param("userId") String userId,
        @Param("isSuccess") LoginSuccess isSuccess, @Param("isStatus") Boolean status,
        Pageable pageable);

    //
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE UserLoginLog u SET u.defaultLocationLog.isStatus = :isStatus where u.defaultLocationLog.userId = :userId AND u.isSuccess =:isSuccess")
    void isStatusUpdate(@Param("userId") String userId, @Param("isSuccess") LoginSuccess isSuccess,
        @Param("isStatus") boolean isStatus);

    //
    @Query(value = "select u from UserLoginLog u where "
        + "u.defaultLocationLog.userId = :userId and "
        + "u.isSuccess = :isSuccess and "
        + "u.defaultLocationLog.isStatus = :isStatus and DATE_FORMAT(u.createdDateTime,'%Y-%m-%d') between :startDate and :endDate")
    Page<UserLoginLog> getLoginLogPageable(@Param("userId") String userId,
        @Param("isSuccess") LoginSuccess isSuccess,
        @Param("isStatus") Boolean status,
        @Param("startDate") LocalDate startDate,
        @Param("endDate")  LocalDate endDate,
        Pageable pageable);





    @Query(value = "select count (u.id) from UserLoginLog u where "
        + "u.defaultLocationLog.isStatus = :isStatus and DATE_FORMAT(u.createdDateTime,'%Y-%m-%d') < :expireDate ")
    Integer expireLoginLogCount(@Param("isStatus") Boolean status, @Param("expireDate") LocalDate expireDate);


    @Modifying(clearAutomatically = true)
    @Query("update UserLoginLog u set u.defaultLocationLog.isStatus = :offStatus where  u.defaultLocationLog.isStatus = :onStatus"
        + " and  DATE_FORMAT(u.createdDateTime,'%Y-%m-%d') < :expireDate")
    Integer expireLoginLogOff (@Param("offStatus") Boolean offStatus,@Param("onStatus")
    Boolean onStatus ,@Param("expireDate") LocalDate expireDate);




}

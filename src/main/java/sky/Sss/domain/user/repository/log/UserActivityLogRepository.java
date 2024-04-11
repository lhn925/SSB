package sky.Sss.domain.user.repository.log;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.UserActivityLog;
import sky.Sss.domain.user.model.ChangeSuccess;

public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {


    @Query(value = "select u from UserActivityLog u where u.uId = :uid "
        + " and u.changeSuccess = :changeSuccess "
        + " and u.defaultLog.isStatus = :isStatus "
        + " and DATE_FORMAT(u.createdDateTime,'%Y-%m-%d') between :startDate and :endDate ")
    Page<UserActivityLog> getPagedDataByUid(@Param("uid") User uid, @Param("changeSuccess") ChangeSuccess changeSuccess,
        @Param("isStatus") boolean isStatus, @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
       Pageable pageable);


    @Query(value = "select count (u.id) from UserActivityLog u where "
        + "u.defaultLog.isStatus = :isStatus and DATE_FORMAT(u.createdDateTime,'%Y-%m-%d') < :expireDate ")
    Integer expireActivityCount(@Param("isStatus") Boolean isStatus, @Param("expireDate") LocalDate expireDate);

    @Modifying(clearAutomatically = true)
    @Query(
        value = "update UserActivityLog u set u.defaultLog.isStatus = :offStatus where  u.defaultLog.isStatus = :onStatus"
            + " and  DATE_FORMAT(u.createdDateTime,'%Y-%m-%d') < :expireDate")
    Integer expireActivityOff(@Param("offStatus") Boolean offStatus, @Param("onStatus") Boolean onStatus,@Param("expireDate") LocalDate expireDate);
}

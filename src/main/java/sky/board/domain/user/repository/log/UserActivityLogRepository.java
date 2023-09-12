package sky.board.domain.user.repository.log;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.board.domain.user.entity.UserActivityLog;
import sky.board.domain.user.model.ChangeSuccess;

public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {


    @Query(value = "select u from UserActivityLog u where u.uId = :uid"
        + " and u.changeSuccess = :changeSuccess"
        + " and u.isStatus = :isStatus"
        + " and DATE_FORMAT(u.createdDateTime,'%Y-%m-%d') between :startDate and :endDate")
    Page<UserActivityLog> getPagedDataByUid(@Param("uid") Long uid, @Param("changeSuccess") ChangeSuccess changeSuccess,
        @Param("isStatus") Boolean isStatus, @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Pageable pageable);

}

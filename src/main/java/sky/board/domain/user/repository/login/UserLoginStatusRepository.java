package sky.board.domain.user.repository.login;


import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import sky.board.domain.user.entity.User;
import sky.board.domain.user.entity.login.UserLoginStatus;
import sky.board.domain.user.model.Status;

public interface UserLoginStatusRepository extends JpaRepository<UserLoginStatus, Long> {

    List<UserLoginStatus> findAllByUidAndLoginStatusAndSessionNot(User user, Boolean loginStatus,String session);

    @Query(value = "select u from UserLoginStatus u where u.uid = :uid and u.defaultLoginLog.userId = :userId and u.session = :session")
    Optional<UserLoginStatus> findOne(@Param("uid") User user, @Param("userId") String userId,
        @Param("session") String session);


    @Modifying(clearAutomatically = true)
    @Query(value =
        "update UserLoginStatus u set u.loginStatus = :loginStatus , u.defaultLoginLog.isStatus = :isStatus where "
            + "u.uid = :uid")
    Integer updateAll(@Param("uid") User user, @Param("loginStatus") Boolean loginStatus,
        @Param("isStatus") Boolean isStatus);
}

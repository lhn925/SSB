package sky.Sss.domain.user.repository.login;


import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.login.UserLoginStatus;

public interface UserLoginStatusRepository extends JpaRepository<UserLoginStatus, Long> {

    List<UserLoginStatus> findAllByUidAndLoginStatusAndSessionNot(User user, Boolean loginStatus, String session);


    Page<UserLoginStatus> findByUidAndLoginStatus(User user, Boolean loginStatus, Pageable pageable);

    @Query(value = "select u from UserLoginStatus u where u.uid = :uid and u.defaultLoginLog.userId = :userId and u.session = :session")
    Optional<UserLoginStatus> findOne(@Param("uid") User user, @Param("userId") String userId,
        @Param("session") String session);


    @Query(value = "select u from UserLoginStatus u where u.uid = :uid and u.defaultLoginLog.userId = :userId and u.session = :session")
    List<UserLoginStatus> findList(@Param("uid") User user, @Param("userId") String userId,
        @Param("session") String session);


    @Query(value =
        "select u from UserLoginStatus u where u.uid = :uid and u.defaultLoginLog.userId = :userId and u.session = :session"
            + " and u.loginStatus = :loginStatus and u.defaultLoginLog.isStatus =:isStatus")
    List<UserLoginStatus> findList(@Param("uid") User user, @Param("userId") String userId,
        @Param("session") String session, @Param("loginStatus") Boolean loginStatus,
        @Param("isStatus") Boolean isStatus);

    @Query(value =
        "select u from UserLoginStatus u where u.uid = :uid and u.defaultLoginLog.userId = :userId and u.session = :session"
            + " and u.remember =:remember  and u.loginStatus = :loginStatus and u.defaultLoginLog.isStatus =:isStatus")
    List<UserLoginStatus> findList(@Param("uid") User user, @Param("userId") String userId,
        @Param("session") String session, @Param("remember") String remember, @Param("loginStatus") Boolean loginStatus,
        @Param("isStatus") Boolean isStatus);


    @Modifying(clearAutomatically = true)
    @Query(value =
        "update UserLoginStatus u set u.loginStatus = :loginStatus , u.defaultLoginLog.isStatus = :isStatus where "
            + "u.uid = :uid")
    Integer updateAll(@Param("uid") User user, @Param("loginStatus") Boolean loginStatus,
        @Param("isStatus") Boolean isStatus);

    @Modifying(clearAutomatically = true)
    @Query(value =
        "update UserLoginStatus u set u.loginStatus = :loginStatus , u.defaultLoginLog.isStatus = :isStatus where "
            + "u.uid = :uid and u.session = :session")
    Integer updateAllAndNotSession(@Param("uid") User user, @Param("session") String session,
        @Param("loginStatus") Boolean loginStatus,
        @Param("isStatus") Boolean isStatus);


    @Modifying(clearAutomatically = true)
    @Query(value =
        "update UserLoginStatus u set u.loginStatus = :loginStatus , u.defaultLoginLog.isStatus = :isStatus where "
            + "u.uid = :uid and u.session = :session")
    Integer update(@Param("uid") User user, @Param("loginStatus") Boolean loginStatus,
        @Param("isStatus") Boolean isStatus, @Param("session") String session);

    @Modifying(clearAutomatically = true)
    @Query(value =
        "update UserLoginStatus u set u.loginStatus = :loginStatus , u.defaultLoginLog.isStatus = :isStatus where "
            + "u.uid = :uid and u.session = :session and u.remember = :remember")
    Integer update(@Param("uid") User user, @Param("loginStatus") Boolean loginStatus,
        @Param("isStatus") Boolean isStatus, @Param("session") String session, @Param("remember") String remember);


    @Modifying(clearAutomatically = true)
    @Query(value =
        "update UserLoginStatus u set u.loginStatus = :loginStatus , u.defaultLoginLog.isStatus = :isStatus where u.session = :session")
    Integer updateSession(@Param("loginStatus") Boolean loginStatus,
        @Param("isStatus") Boolean isStatus, @Param("session") String session);

    @Modifying(clearAutomatically = true)
    @Query(value =
        "update UserLoginStatus u set u.loginStatus = :loginStatus , u.defaultLoginLog.isStatus = :isStatus where "
            + " u.remember = :remember")
    Integer updateRemember(@Param("loginStatus") Boolean loginStatus,
        @Param("isStatus") Boolean isStatus, @Param("remember") String remember);


}

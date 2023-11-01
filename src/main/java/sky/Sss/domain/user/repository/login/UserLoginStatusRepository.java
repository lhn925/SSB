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

    List<UserLoginStatus> findAllByUidAndLoginStatus(User user, Boolean loginStatus);

    Page<UserLoginStatus> findByUidAndLoginStatus(User user, Boolean loginStatus, Pageable pageable);

    @Query(value = "select u from UserLoginStatus u where u.uid = :uid and u.defaultLoginLog.userId = :userId and u.refreshToken = :refreshToken and u.loginStatus = :loginStatus and u.defaultLoginLog.isStatus = :isStatus")
    Optional<UserLoginStatus> findOne(@Param("uid") User user, @Param("userId") String userId,
        @Param("refreshToken") String redisToken,@Param("loginStatus") Boolean loginStatus,@Param("isStatus") Boolean isStatus);

    @Query(value = "select u from UserLoginStatus u where u.uid = :uid and u.defaultLoginLog.userId = :userId and u.redisToken = :redisToken and u.refreshToken = :refreshToken and u.loginStatus = :loginStatus and u.defaultLoginLog.isStatus = :isStatus")
    Optional<UserLoginStatus> findOne(@Param("uid") User user, @Param("userId") String userId,
        @Param("redisToken") String redisToken,@Param("refreshToken") String refreshToken,@Param("loginStatus") Boolean loginStatus,@Param("isStatus") Boolean isStatus);


    @Query(value = "select u from UserLoginStatus u where u.uid = :uid and u.defaultLoginLog.userId = :userId and u.redisToken = :redisToken")
    List<UserLoginStatus> findList(@Param("uid") User user, @Param("userId") String userId,
        @Param("redisToken") String redisToken);


    @Query(value =
        "select u from UserLoginStatus u where u.uid = :uid and u.defaultLoginLog.userId = :userId and u.sessionId = :sessionId"
            + " and u.loginStatus = :loginStatus and u.defaultLoginLog.isStatus =:isStatus")
    List<UserLoginStatus> findList(@Param("uid") User user, @Param("userId") String userId, @Param("loginStatus") Boolean loginStatus,
        @Param("isStatus") Boolean isStatus,@Param("sessionId") String sessionId);



    @Modifying(clearAutomatically = true)
    @Query(value =
        "update UserLoginStatus u set u.loginStatus = :loginStatus , u.defaultLoginLog.isStatus = :isStatus where "
            + "u.uid = :uid")
    Integer updateAll(@Param("uid") User user, @Param("loginStatus") Boolean loginStatus,
        @Param("isStatus") Boolean isStatus);

    @Modifying(clearAutomatically = true)
    @Query(value =
        "update UserLoginStatus u set u.loginStatus = :loginStatus , u.defaultLoginLog.isStatus = :isStatus where "
            + "u.uid = :uid")
    Integer updateAllAndNotRedisToken(@Param("uid") User user,
        @Param("loginStatus") Boolean loginStatus,
        @Param("isStatus") Boolean isStatus);


    @Modifying(clearAutomatically = true)
    @Query(value =
        "update UserLoginStatus u set u.loginStatus = :loginStatus , u.defaultLoginLog.isStatus = :isStatus where "
            + "u.uid = :uid and u.sessionId = :sessionId")
    Integer update(@Param("uid") User user, @Param("loginStatus") Boolean loginStatus,
        @Param("isStatus") Boolean isStatus, @Param("sessionId") String sessionId);

    @Modifying(clearAutomatically = true)
    @Query(value =
        "update UserLoginStatus u set u.loginStatus = :loginStatus , u.defaultLoginLog.isStatus = :isStatus where u.redisToken = :redisToken")
    Integer updateSession(@Param("loginStatus") Boolean loginStatus,
        @Param("isStatus") Boolean isStatus, @Param("redisToken") String redisToken);


}

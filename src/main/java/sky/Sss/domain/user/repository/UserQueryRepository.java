package sky.Sss.domain.user.repository;


import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.user.dto.myInfo.UserMyInfoDto;
import sky.Sss.domain.user.entity.User;


public interface UserQueryRepository extends JpaRepository<User, Long> {

//    Optional<User> findByUserId(String userId);

    /**
     * 탈퇴 된 회원은 검색하지 않음
     *
     * @param userId
     * @return
     */
    @Query("select u from User u where u.userId = :userId and u.isEnabled = true")
    Optional<User> findByUserId(@Param("userId") String userId);

    /**
     * 탈퇴 여부 값 확인
     *
     * @param email
     * @param isEnabled
     * @return
     */
    Optional<User> findByEmailAndIsEnabled(String email, Boolean isEnabled);

    Optional<User> findByUserIdAndIsEnabled(String userId, Boolean isEnabled);





    /**
     * 탈퇴 된 회원은 검색하지 않음
     *
     * @param userId
     * @return
     */
    @Query("select new sky.Sss.domain.user.dto.myInfo.UserMyInfoDto(u.userId,u.email,u.userName,u.pictureUrl,u.isLoginBlocked,u.grade) from User u where u.userId = :userId and u.isEnabled = true")
    Optional<UserMyInfoDto> getUserMyInfoDto(@Param("userId") String userId);


    /**
     * 탈퇴 된 회원은 검색하지 않음
     *
     * @param userId
     * @param token
     * @return
     */
    @Query("select u from User u "
        + " where u.userId =:userId and u.token = :token and u.isEnabled = true")
    Optional<User> findOne(@Param("userId") String userId, @Param("token") String token);

    Optional<User> findByIdAndIsEnabled(Long uid, Boolean isEnabled);



    @Query("select u from User u where u.userName in (:userNames) and u.isEnabled = :isEnabled")
    Set<User> findAllByUserNames(@Param("userNames") Set<String> userNames,@Param("isEnabled") Boolean isEnabled);


    @Query("select u from User u where u.userName = :userName and u.isEnabled = :isEnabled")
    Optional<User> findByUserName(@Param("userName") String userName,@Param("isEnabled") Boolean isEnabled);


    // 중복 값
    Boolean existsBySalt(String slat);

    Boolean existsByUserName(String username);

    Boolean existsByUserId(String userid);

    Boolean existsByEmail(String Email);
}

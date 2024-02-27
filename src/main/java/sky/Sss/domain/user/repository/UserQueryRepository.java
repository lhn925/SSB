package sky.Sss.domain.user.repository;


import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Enabled;


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
     * @param token
     * @return
     */
    @Query("select u from User u "
        + " where u.userId =:userId and u.token = :token and u.isEnabled = true")
    Optional<User> findOne(@Param("userId") String userId, @Param("token") String token);

    Optional<User> findByIdAndIsEnabled(Long uid, Boolean isEnabled);


    // 중복 값
    Boolean existsBySalt(String slat);

    Boolean existsByUserName(String username);

    Boolean existsByUserId(String userid);

    Boolean existsByEmail(String Email);
}

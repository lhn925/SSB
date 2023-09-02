package sky.board.domain.user.repository;


import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.board.domain.user.entity.User;


public interface UserQueryRepository extends JpaRepository<User, Long> {

//    Optional<User> findByUserId(String userId);

    Optional<User> findByUserId(String userId);

    Optional<User> findByEmail(String email);

    Optional<User> findByUserIdAndIsStatus(String userId, Boolean isStatus);


    @Query("select u from User u "
        + " where u.userId =:userId and u.token = :token")
    Optional<User> findOne(@Param("userId") String userId, @Param("token") String token);


    // 중복 값
    Boolean existsBySalt(String slat);

    Boolean existsByUserName(String username);

    Boolean existsByUserId(String userid);

    Boolean existsByEmail(String Email);
}

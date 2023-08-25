package sky.board.domain.user.repository;


import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import sky.board.domain.user.entity.User;
import sky.board.domain.user.model.Status;


public interface UserQueryRepository extends JpaRepository<User,Long>{


//    Optional<User> findByUserId(String userId);

    User findByUserId(String userId);


    Optional<User> findByEmail(String email);
    Optional<User> findByUserIdAndIsStatus(String userId, Boolean isStatus);
    // 중복 값
    Boolean existsBySalt(String slat);
    Boolean existsByUserName(String username);
    Boolean existsByUserId(String userid);
    Boolean existsByEmail(String Email);
}

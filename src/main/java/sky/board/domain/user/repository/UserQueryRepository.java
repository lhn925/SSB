package sky.board.domain.user.repository;


import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import sky.board.domain.user.entity.User;


public interface UserQueryRepository extends JpaRepository<User,Long>{


    Optional<User> findByUserId(String userId);

    // 중복 값
    Boolean existsBySalt(String slat);
    Boolean existsByUserName(String username);
    Boolean existsByUserId(String userid);
    Boolean existsByEmail(String Email);
}

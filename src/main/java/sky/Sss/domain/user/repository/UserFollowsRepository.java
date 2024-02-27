package sky.Sss.domain.user.repository;


import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.UserFollows;

public interface UserFollowsRepository extends JpaRepository<UserFollows,Long> {
    Optional<UserFollows> findByFollowingUserAndFollowerUser(User followingUser, User followerUser);



    // 유저가 팔로워 하고 있는 총 유저 수
    Integer countByFollowerUser(User followerUser);

    // 유저를 팔로워 하고 있는 총 유저 수
    Integer countByFollowingUser(User followingUser);
}
